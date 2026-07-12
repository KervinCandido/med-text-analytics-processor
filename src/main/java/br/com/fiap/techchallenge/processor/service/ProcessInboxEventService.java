package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxEvent;
import br.com.fiap.techchallenge.processor.dto.DocumentMetaDataDTO;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEventStatus;
import br.com.fiap.techchallenge.processor.service.ia.DocumentExtractDataIAStrategy;
import br.com.fiap.techchallenge.processor.service.ia.classify.ClassifyDocumentIAService;
import dev.langchain4j.data.image.Image;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;

@ApplicationScoped
public class ProcessInboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInboxEventService.class);

    private final ClassifyDocumentIAService classifyDocumentIAService;
    private final DocumentExtractDataIAStrategy documentExtractDataIAStrategy;

    @Inject
    public ProcessInboxEventService(ClassifyDocumentIAService classifyDocumentIAService, DocumentExtractDataIAStrategy documentExtractDataIAStrategy) {
        this.classifyDocumentIAService = classifyDocumentIAService;
        this.documentExtractDataIAStrategy = documentExtractDataIAStrategy;
    }

    public void process(InboxEvent inboxEvent) {
        logger.info("action=processInboxEventStart, inboxEventId={}", inboxEvent.getInboxId());
        inboxEvent.processing();
        inboxEvent.persistOrUpdate();

        final var userId = inboxEvent.getUserId();
        var outboxEvent = new OutboxEvent();

        for (String filePath : inboxEvent.getFilePaths()) {
            try {
                Path path = Paths.get(filePath);
                byte[] fileContent = Files.readAllBytes(path);
                String base64Image = Base64.getEncoder().encodeToString(fileContent);
                String mimeType = getMimeType(filePath);
                Image image = Image.builder().base64Data(base64Image).mimeType(mimeType).build();
                DocumentMetaDataDTO documentMetaDataDTO = classifyDocumentIAService.classifyDocument(image);
                for (DocumentType docType : documentMetaDataDTO.getClassifications()) {
                    var document = documentExtractDataIAStrategy.get(docType).extractData(image);
                    document.setDocumentType(docType);
                    document.setUserId(userId);
                    document.persist();
                    outboxEvent.addDocument(document);
                }
            } catch (IOException e) {
                logger.error("action=readFileFailed, filePath={}, reason={}", filePath, e.getMessage());
            }
        }

        outboxEvent.persist();
        inboxEvent.setStatus(InboxEventStatus.PROCESSED);
        inboxEvent.processed();
        inboxEvent.persistOrUpdate();
    }

    private String getMimeType(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
