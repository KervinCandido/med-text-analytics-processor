package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
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
import dev.langchain4j.exception.RateLimitException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

@ApplicationScoped
public class ProcessDocumentRequestService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProcessDocumentRequestService.class);

    private static final String STORAGE_READ_ERROR =
            "STORAGE_READ_ERROR";

    private static final String AI_QUOTA_EXCEEDED =
            "AI_QUOTA_EXCEEDED";

    private static final String AI_PROCESSING_ERROR =
            "AI_PROCESSING_ERROR";

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
            ObjectIdMapper objectIdMapper,
            NextcloudStorageService nextcloudStorageService
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

    public void process(InboxDocumentProcessingRequest inbox) {
        logger.info(
                "action=processInboxEventStart, inboxEventId={}, documentId={}",
                inbox.getEventId(),
                inbox.getDocumentId()
        );

        inbox.processing();

        inboxRepository.persistOrUpdate(
                inboxMapper.toEntity(inbox)
        );

        OutboxDocumentResponse outboxEvent =
                createOutboxEvent(inbox);

        String filePath = inbox.getFilePath();

        try {
            Image image = buildImage(filePath);

            var documentMetaData =
                    classifyDocumentIAService.classifyDocument(image);

            for (DocumentType documentType
                    : documentMetaData.getClassifications()) {

                var document = documentExtractDataIAStrategy
                        .get(documentType)
                        .extractData(image);

                document.setDocumentType(documentType);
                document.setPatientId(inbox.getPatientId());
                document.applyDocumentDateWithFallback(
                        inbox.getCreatedAt()
                );

                var documentEntity =
                        documentoMapper.toEntity(document);

                documentoRepository.persist(documentEntity);

                outboxEvent.addDocumentId(
                        objectIdMapper.map(documentEntity.getId())
                );
            }

            outboxEvent.markSuccessfulResponse();

            outboxRepository.persistOrUpdate(
                    outboxMapper.toEntity(outboxEvent)
            );

            inbox.processed();

            logger.info(
                    "action=processInboxEventSuccess, eventId={}, documentId={}",
                    inbox.getEventId(),
                    inbox.getDocumentId()
            );
        } catch (IOException exception) {
            registerRetriableFailure(
                    inbox,
                    outboxEvent,
                    STORAGE_READ_ERROR,
                    "Não foi possível ler o arquivo armazenado."
            );

            logger.error(
                    "action=readFileFailed, eventId={}, documentId={}, reason={}",
                    inbox.getEventId(),
                    inbox.getDocumentId(),
                    exception.getMessage()
            );
        } catch (RateLimitException exception) {
            registerPermanentFailure(
                    inbox,
                    outboxEvent,
                    AI_QUOTA_EXCEEDED,
                    "O limite de uso do serviço de inteligência artificial foi excedido."
            );

            logger.warn(
                    "action=aiQuotaExceeded, eventId={}, documentId={}",
                    inbox.getEventId(),
                    inbox.getDocumentId()
            );
        } catch (Exception exception) {
            registerRetriableFailure(
                    inbox,
                    outboxEvent,
                    AI_PROCESSING_ERROR,
                    "Não foi possível processar o documento pela inteligência artificial."
            );

            logger.error(
                    "action=processInboxEventError, eventId={}, documentId={}, reason={}",
                    inbox.getEventId(),
                    inbox.getDocumentId(),
                    exception.getMessage(),
                    exception
            );
        } finally {
            inboxRepository.persistOrUpdate(
                    inboxMapper.toEntity(inbox)
            );
        }
    }

    private OutboxDocumentResponse createOutboxEvent(
            InboxDocumentProcessingRequest inbox
    ) {
        OutboxDocumentResponse outboxEvent =
                new OutboxDocumentResponse();

        outboxEvent.setDocumentId(inbox.getDocumentId());
        outboxEvent.setEventId(inbox.getEventId());
        outboxEvent.setPatientId(inbox.getPatientId());

        return outboxEvent;
    }

    private void registerRetriableFailure(
            InboxDocumentProcessingRequest inbox,
            OutboxDocumentResponse outboxEvent,
            String errorCode,
            String errorMessage
    ) {
        inbox.failed();

        if (ProcessingStatus.ALL_RETRY_FAILED.equals(
                inbox.getStatus()
        )) {
            persistFailureResponse(
                    outboxEvent,
                    errorCode,
                    errorMessage
            );
        }
    }

    private void registerPermanentFailure(
            InboxDocumentProcessingRequest inbox,
            OutboxDocumentResponse outboxEvent,
            String errorCode,
            String errorMessage
    ) {
        inbox.failPermanently();

        persistFailureResponse(
                outboxEvent,
                errorCode,
                errorMessage
        );
    }

    private void persistFailureResponse(
            OutboxDocumentResponse outboxEvent,
            String errorCode,
            String errorMessage
    ) {
        outboxEvent.markFailedResponse(
                errorCode + ": " + errorMessage
        );

        outboxRepository.persistOrUpdate(
                outboxMapper.toEntity(outboxEvent)
        );
    }

    private Image buildImage(String filePath) throws IOException {
        final byte[] fileContent;

        try {
            fileContent = nextcloudStorageService.load(filePath);
        } catch (RuntimeException exception) {
            throw new IOException(
                    "Não foi possível ler o arquivo armazenado.",
                    exception
            );
        }

        String base64Image =
                Base64.getEncoder().encodeToString(fileContent);

        return Image.builder()
                .base64Data(base64Image)
                .mimeType(getMimeType(filePath))
                .build();
    }

    private String getMimeType(String filePath) {
        String normalizedPath = filePath.toLowerCase();

        if (normalizedPath.endsWith(".png")) {
            return "image/png";
        }

        if (normalizedPath.endsWith(".jpeg")
                || normalizedPath.endsWith(".jpg")) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }
}
