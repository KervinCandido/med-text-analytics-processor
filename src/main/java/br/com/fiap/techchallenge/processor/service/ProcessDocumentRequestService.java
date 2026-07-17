package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
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
public class ProcessDocumentRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDocumentRequestService.class);

    private final ClassifyDocumentIAService classifyDocumentIAService;
    private final DocumentExtractDataIAStrategy documentExtractDataIAStrategy;
    private final DocumentoRepository documentoRepository;
    private final InboxDocumentProcessingRequestRepository inboxRepository;
    private final OutboxDocumentProcessedResponseRepository outboxRepository;
    private final InboxDocumentProcessingRequestMapper inboxMapper;
    private final DocumentoMapper documentoMapper;
    private final OutboxDocumentResponseMapper outboxMapper;
    private final ObjectIdMapper objectIdMapper;
    private final NextcloudStorageService nextcloudStorageService;

    @Inject
    public ProcessDocumentRequestService(
            ClassifyDocumentIAService classifyDocumentIAService,
            DocumentExtractDataIAStrategy documentExtractDataIAStrategy,
            DocumentoRepository documentoRepository,
            InboxDocumentProcessingRequestRepository inboxRepository,
            OutboxDocumentProcessedResponseRepository outboxRepository,
            InboxDocumentProcessingRequestMapper inboxMapper,
            DocumentoMapper documentoMapper,
            OutboxDocumentResponseMapper outboxMapper,
            ObjectIdMapper objectIdMapper, NextcloudStorageService nextcloudStorageService
    ) {
        this.classifyDocumentIAService = classifyDocumentIAService;
        this.documentExtractDataIAStrategy = documentExtractDataIAStrategy;
        this.documentoRepository = documentoRepository;
        this.inboxRepository = inboxRepository;
        this.outboxRepository = outboxRepository;
        this.inboxMapper = inboxMapper;
        this.documentoMapper = documentoMapper;
        this.outboxMapper = outboxMapper;
        this.objectIdMapper = objectIdMapper;
        this.nextcloudStorageService = nextcloudStorageService;
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
    @Fallback(value = ProcessDocumentRequestFallback.class)
    @ExponentialBackoff
    @RateLimit
    public void process(InboxDocumentProcessingRequest inbox) {
        logger.info("action=processInboxEventStart, InboxDocumentProcessingRequestEntityId={}", inbox.getId());
        inbox.processing();
        inboxRepository.persistOrUpdate(inboxMapper.toEntity(inbox));

        var patientId = inbox.getPatientId();

        var outboxEvent = new OutboxDocumentResponse();
        outboxEvent.setDocumentId(inbox.getDocumentId());
        outboxEvent.setEventId(inbox.getEventId());
        outboxEvent.setPatientId(patientId);

        String filePath = inbox.getFilePath();

        try {
            Image image = buildImage(filePath);
            var documentMetaDataDTO = classifyDocumentIAService.classifyDocument(image);
            for (DocumentType docType : documentMetaDataDTO.getClassifications()) {
                var document = documentExtractDataIAStrategy.get(docType).extractData(image);
                document.setDocumentType(docType);
                document.setPatientId(patientId);
                document.applyDocumentDateWithFallback(inbox.getCreatedAt());
                var docEntity = documentoMapper.toEntity(document);
                documentoRepository.persist(docEntity);
                outboxEvent.addDocumentId(objectIdMapper.map(docEntity.getId()));
            }
            outboxRepository.persistOrUpdate(outboxMapper.toEntity(outboxEvent));
            inbox.processed();
        } catch (IOException e) {
            inbox.failed();
            logger.error("action=readFileFailed, filePath={}, reason={}", filePath, e.getMessage());
        } catch (Exception e) {
            logger.error("action=processInboxEventError, reason={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        inboxRepository.persistOrUpdate(inboxMapper.toEntity(inbox));
    }

    @ApplicationScoped
    public static class ProcessDocumentRequestFallback implements FallbackHandler<Void> {

        private final InboxDocumentProcessingRequestRepository inboxRepository;
        private final InboxDocumentProcessingRequestMapper inboxMapper;

        @Inject
        private ProcessDocumentRequestFallback(InboxDocumentProcessingRequestRepository inboxRepository, InboxDocumentProcessingRequestMapper inboxMapper) {
            this.inboxRepository = inboxRepository;
            this.inboxMapper = inboxMapper;
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
                inboxRepository.persistOrUpdate(inboxMapper.toEntity(inbox));
            }
            return null;
        }
    }

    private Image buildImage(String filePath) throws IOException {
        byte[] fileContent = nextcloudStorageService.load(filePath);
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
