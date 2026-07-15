package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxEventEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
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
import java.util.UUID;

@ApplicationScoped
public class ProcessInboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInboxEventService.class);

    private final ClassifyDocumentIAService classifyDocumentIAService;
    private final DocumentExtractDataIAStrategy documentExtractDataIAStrategy;
    private final DocumentoRepository documentoRepository;
    private final InboxDocumentProcessingRequestRepository inboxRepository;
    private final ObjectIdMapper objectIdMapper;

    @Inject
    public ProcessInboxEventService (
            ClassifyDocumentIAService classifyDocumentIAService,
            DocumentExtractDataIAStrategy documentExtractDataIAStrategy,
            DocumentoRepository documentoRepository, InboxDocumentProcessingRequestRepository inboxRepository, ObjectIdMapper objectIdMapper
    ) {
        this.classifyDocumentIAService = classifyDocumentIAService;
        this.documentExtractDataIAStrategy = documentExtractDataIAStrategy;
        this.documentoRepository = documentoRepository;
        this.inboxRepository = inboxRepository;
        this.objectIdMapper = objectIdMapper;
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
    public void process(InboxDocumentProcessingRequest inbox) {
        logger.info("action=processInboxEventStart, InboxDocumentProcessingRequestEntityId={}", inbox.getId());
        inbox.processing();
        inboxRepository.updateStatus(objectIdMapper.map(inbox.getId()), inbox.getStatus());

        final var patientId = inbox.getPatientId();
        var outboxEvent = new OutboxEventEntity();
        String filePath = inbox.getFilePath();

        try {
            Path path = Paths.get(filePath);
            Image image = buildImage(path, filePath);
            var documentMetaDataDTO = classifyDocumentIAService.classifyDocument(image);
            for (DocumentType docType : documentMetaDataDTO.getClassifications()) {
                var document = documentExtractDataIAStrategy.get(docType).extractData(image);
                document.setDocumentType(docType);
                document.setPatientId(patientId);
                document.applyDocumentDateWithFallback(inbox.getCreatedAt());
                documentoRepository.persist(document);
                outboxEvent.addDocumentId(document.getId());
            }
            outboxEvent.persist();
            inbox.processed();
        } catch (IOException e) {
            logger.error("action=readFileFailed, filePath={}, reason={}", filePath, e.getMessage());
        } catch (Exception e) {
            logger.error("action=processInboxEventError, reason={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static class ProcessFallback implements FallbackHandler<Void> {

        private final InboxDocumentProcessingRequestRepository inboxRepository;
        private final ObjectIdMapper objectIdMapper;

        private ProcessFallback(InboxDocumentProcessingRequestRepository inboxRepository, ObjectIdMapper objectIdMapper) {
            this.inboxRepository = inboxRepository;
            this.objectIdMapper = objectIdMapper;
        }

        @Override
        public Void handle(ExecutionContext context) {
            Throwable failure = context.getFailure();
            Object[] parameters = context.getParameters();

            if (parameters[0] instanceof InboxDocumentProcessingRequest inbox) {
                if (failure instanceof RateLimitException) {
                    inbox.reprocessByRateLimit();
                    logger.info("action=processInboxEventFallbackRateLimit, inboxEventId={}, documentId={}", inbox.getEventId(), inbox.getDocumentId());
                } else {
                    inbox.failed();
                    logger.info("action=processInboxEventFallback, inboxEventId={}, documentId={}", inbox.getEventId(), inbox.getDocumentId());
                }
                inboxRepository.updateStatus(objectIdMapper.map(inbox.getId()), inbox.getStatus());
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
