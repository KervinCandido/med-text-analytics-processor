package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEventStatus;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxEvent;
import br.com.fiap.techchallenge.processor.service.ia.DocumentExtractDataIAStrategy;
import br.com.fiap.techchallenge.processor.service.ia.classify.ClassifyDocumentIAService;
import dev.langchain4j.data.image.Image;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import io.smallrye.faulttolerance.api.RateLimit;
import io.smallrye.faulttolerance.api.RateLimitException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
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

    @Retry(
            maxRetries = 5,
            delay = 5,
            delayUnit = ChronoUnit.SECONDS,
            jitter = 150,
            jitterDelayUnit = ChronoUnit.MILLIS
    )
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.4,
            delay = 30,
            delayUnit = ChronoUnit.SECONDS
    )
    @Fallback(value = ProcessFallback.class)
    @ExponentialBackoff
    @RateLimit
    public void process(InboxEvent inboxEvent) {
        logger.info("action=processInboxEventStart, inboxEventId={}", inboxEvent.getEventId());
        inboxEvent.processing();
        inboxEvent.persistOrUpdate();

        final var patientId = inboxEvent.getPatientId();
        var outboxEvent = new OutboxEvent();
        String filePath = inboxEvent.getFilePath();

        try {
            Path path = Paths.get(filePath);
            Image image = buildImage(path, filePath);
            var documentMetaDataDTO = classifyDocumentIAService.classifyDocument(image);
            for (DocumentType docType : documentMetaDataDTO.getClassifications()) {
                var document = documentExtractDataIAStrategy.get(docType).extractData(image);
                document.setDocumentType(docType);
                document.setPatientId(patientId);
                document.persist();
                outboxEvent.addDocument(document);
            }
            outboxEvent.persist();
            inboxEvent.setStatus(InboxEventStatus.PROCESSED);
            inboxEvent.processed();
            inboxEvent.persistOrUpdate();
        } catch (IOException e) {
            logger.error("action=readFileFailed, filePath={}, reason={}", filePath, e.getMessage());
        } catch (Exception e) {
            logger.error("action=processInboxEventError, reason={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static class ProcessFallback implements FallbackHandler<Void> {
        @Override
        public Void handle(ExecutionContext context) {
            Throwable failure = context.getFailure();
            Object[] parameters = context.getParameters();

            if (parameters[0] instanceof InboxEvent inboxEvent) {
                if (failure instanceof RateLimitException) {
                    inboxEvent.reprocessByRateLimit();
                    logger.info("action=processInboxEventFallbackRateLimit, inboxEventId={}, documentId={}", inboxEvent.getEventId(), inboxEvent.getDocumentId());
                } else {
                    inboxEvent.failed();
                    logger.info("action=processInboxEventFallback, inboxEventId={}, documentId={}", inboxEvent.getEventId(), inboxEvent.getDocumentId());
                }
                inboxEvent.persistOrUpdate();
            }
            return null;
        }
    }

    private Image buildImage(Path path, String filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(path);
        String base64Image = Base64.getEncoder().encodeToString(fileContent);
        String mimeType = getMimeType(filePath);
        return Image.builder()
                .base64Data(base64Image)
                .mimeType(mimeType)
                .build();
    }

    private String getMimeType(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
