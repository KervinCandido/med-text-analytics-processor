package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJob;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEventStatus;
import br.com.fiap.techchallenge.processor.strategy.DocumentProcessingStrategy;
import br.com.fiap.techchallenge.processor.strategy.DocumentProcessingStrategyRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcessInboxEventServiceBackup {

    private static final ZoneId SAO_PAULO_ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final Logger logger = LoggerFactory.getLogger(ProcessInboxEventServiceBackup.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AiVisionService aiVisionService;

    @Inject
    DocumentProcessingStrategyRegistry strategyRegistry;

    @Inject
    JobStatusUpdater jobStatusUpdater;

    public void process(InboxEvent event) {
        String jobId = event.getInboxId();
        List<String> storedFilePaths = event.getFilePaths() == null ? List.of() : event.getFilePaths();
        event.setStatus(InboxEventStatus.PROCESSING);
        event.update();

        try {
            var job = processDataExtract(event, storedFilePaths, jobId);
            jobStatusUpdater.updateAndQueueStatus(job);
            event.setStatus(InboxEventStatus.PROCESSED);
            event.setProcessedAt(LocalDateTime.now(SAO_PAULO_ZONE_ID));
            event.update();
            logger.info("action=processInboxEventSuccess, eventId={}, jobId={}", event.getInboxId(), jobId);
        } catch (Exception e) {
            logger.error("action=processInboxEventError, eventId={}, jobId={}, reason={}", event.getInboxId(), jobId, e.getMessage(), e);
            event.setStatus(InboxEventStatus.FAILED);
            event.setErrorDetail(e.getMessage());
            event.setProcessedAt(LocalDateTime.now(SAO_PAULO_ZONE_ID));
            event.update();
        }
    }

    private ProcessamentoJob processDataExtract(InboxEvent event, List<String> storedFilePaths, String jobId) {
        var optionalJob = ProcessamentoJob.findById(jobId); // reprocessamento
        ProcessamentoJob job = optionalJob.orElse(new ProcessamentoJob());
        job.setId(jobId);
        job.setUserId(event.getUserId());
        job.setTotalFiles(event.getTotalFiles());
        job.limpaFalhas();

        var filesToProcess = storedFilePaths
                .stream()
                .filter(file -> !job.getImagensSucesso().contains(file))
                .toList();

        for (String storedFilePath : filesToProcess) {
            try {
                List<String> classifications = classifyDocument(storedFilePath);
                logger.info("action=documentClassified, jobId={}, filePath={}, classifications={}", jobId, storedFilePath, classifications);

                List<String> persistedEntitiesJson = new ArrayList<>();
                LocalDateTime uploadTime = LocalDateTime.now(SAO_PAULO_ZONE_ID);
                String fileName = new File(storedFilePath).getName();

                for (String classification : classifications) {
                    String extractedDataJson = aiVisionService.extractDocumentData(storedFilePath, classification);
                    JsonNode dataNode = objectMapper.readTree(extractedDataJson);

                    DocumentProcessingStrategy strategy = strategyRegistry.get(classification);
                    PanacheMongoEntity entity = strategy.process(dataNode, fileName, storedFilePath, uploadTime);

                    enrichDocument(entity, event.getUserId(), uploadTime);
                    entity.persistOrUpdate();
                    persistedEntitiesJson.add(objectMapper.writeValueAsString(entity));
                }
                job.addResultados(persistedEntitiesJson);
                job.incrementeProcessedFiles();
                job.addImagensSucesso(storedFilePath);
            } catch (Exception e) {
                logger.error("action=processInboxEventFileError, jobId={}, filePath={}, reason={}", jobId, storedFilePath, e.getMessage(), e);
                job.addImagensFalha(storedFilePath);
                job.incrementeProcessedFiles();
                job.setErrorDetail("Error on file " + storedFilePath + ": " + e.getMessage());
            }
        }
        job.finish();
        job.persistOrUpdate();
        return job;
    }

    private List<String> classifyDocument(String storedFilePath) throws JsonProcessingException {
        // Classify document using IA Classifier
        String classificationJson = aiVisionService.classifyDocument(storedFilePath);
        JsonNode classNode = objectMapper.readTree(classificationJson);

        List<String> classifications = new ArrayList<>();
        if (classNode.has("classifications") && classNode.get("classifications").isArray()) {
            for (JsonNode classificationItem : classNode.get("classifications")) {
                classifications.add(classificationItem.asText());
            }
        }
        if (classifications.isEmpty()) {
            classifications.add("OUTROS");
        }
        return classifications;
    }

    private void enrichDocument(PanacheMongoEntity entity, String userId, LocalDateTime uploadTime) {
        if (entity instanceof br.com.fiap.techchallenge.processor.domain.exame.Exame e) {
            e.setUserId(userId);
            e.setDocumentDate((e.getDataColeta() != null) ? e.getDataColeta() : (e.getDataLiberacao() != null ? e.getDataLiberacao() : uploadTime));
        } else if (entity instanceof br.com.fiap.techchallenge.processor.domain.laudo.Laudo l) {
            l.setUserId(userId);
            l.setDocumentDate((l.getDataLaudo() != null) ? l.getDataLaudo() : uploadTime);
        } else if (entity instanceof br.com.fiap.techchallenge.processor.domain.receita.Receita r) {
            r.setUserId(userId);
            r.setDocumentDate((r.getDataReceita() != null) ? r.getDataReceita() : uploadTime);
        } else if (entity instanceof br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio rep) {
            rep.setUserId(userId);
            rep.setDocumentDate((rep.getDataRelatorio() != null) ? rep.getDataRelatorio() : uploadTime);
        } else if (entity instanceof br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento enc) {
            enc.setUserId(userId);
            enc.setDocumentDate((enc.getDataEmissao() != null) ? enc.getDataEmissao() : uploadTime);
        }
    }

}
