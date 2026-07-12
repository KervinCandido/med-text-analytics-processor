package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJob;
import br.com.fiap.techchallenge.processor.domain.outbox.ProcessorOutboxEvent;
import br.com.fiap.techchallenge.processor.dto.JobStatusUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class JobStatusUpdater {

    private static final ZoneId SAO_PAULO_ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final Logger logger = LoggerFactory.getLogger(JobStatusUpdater.class);

    @Inject
    ObjectMapper objectMapper;

    public void updateAndQueueStatus(ProcessamentoJob job) {
        job.update();
        try {
            JobStatusUpdateDTO updateDto = new JobStatusUpdateDTO(
                    job.getId(),
                    job.getStatus(),
                    job.getProcessedFiles(),
                    job.getImagensSucesso(),
                    job.getImagensFalha(),
                    job.getResultados(),
                    job.getErrorDetail()
            );
            String payload = objectMapper.writeValueAsString(updateDto);

            ProcessorOutboxEvent event = new ProcessorOutboxEvent();
            event.jobId = job.getId();
            event.payload = payload;
            event.status = "PENDING";
            event.createdAt = LocalDateTime.now(SAO_PAULO_ZONE_ID);
            event.persist();
            
            logger.info("action=queuedJobStatusUpdate, jobId={}, status={}", job.getId(), job.getStatus());
        } catch (Exception e) {
            logger.error("action=queueJobStatusUpdateError, jobId={}, reason={}", job.getId(), e.getMessage(), e);
        }
    }
}
