package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxDocumentResponseEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
import br.com.fiap.techchallenge.processor.service.ia.DocumentExtractDataIAStrategy;
import br.com.fiap.techchallenge.processor.service.ia.classify.ClassifyDocumentIAService;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.exception.RateLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessDocumentRequestServiceTest {

    private static final String FILE_PATH =
            "/documents/storage-object-without-extension";

    private static final String CONTENT_TYPE =
            "image/jpeg";

    @Mock
    private ClassifyDocumentIAService classifyDocumentIAService;

    @Mock
    private DocumentExtractDataIAStrategy
            documentExtractDataIAStrategy;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private InboxDocumentProcessingRequestRepository
            inboxRepository;

    @Mock
    private OutboxDocumentProcessedResponseRepository
            outboxRepository;

    @Mock
    private InboxDocumentProcessingRequestMapper inboxMapper;

    @Mock
    private DocumentoMapper documentoMapper;

    @Mock
    private OutboxDocumentResponseMapper outboxMapper;

    @Mock
    private ObjectIdMapper objectIdMapper;

    @Mock
    private NextcloudStorageService nextcloudStorageService;

    private ProcessDocumentRequestService service;

    @BeforeEach
    void setUp() {
        service = new ProcessDocumentRequestService(
                classifyDocumentIAService,
                documentExtractDataIAStrategy,
                documentoRepository,
                inboxRepository,
                outboxRepository,
                inboxMapper,
                documentoMapper,
                outboxMapper,
                objectIdMapper,
                nextcloudStorageService
        );
    }

    @Test
    void shouldUseInboxContentTypeAndPersistTerminalFailureWhenAiQuotaIsExceeded() {
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        InboxDocumentProcessingRequest inbox =
                new InboxDocumentProcessingRequest(
                        "inbox-id",
                        eventId,
                        documentId,
                        FILE_PATH,
                        CONTENT_TYPE,
                        patientId
                );

        InboxDocumentProcessingRequestEntity inboxEntity =
                new InboxDocumentProcessingRequestEntity();

        OutboxDocumentResponseEntity outboxEntity =
                new OutboxDocumentResponseEntity();

        when(inboxMapper.toEntity(inbox))
                .thenReturn(inboxEntity);

        when(nextcloudStorageService.load(
                inbox.getFilePath()
        )).thenReturn(
                new byte[]{1, 2, 3}
        );

        when(classifyDocumentIAService.classifyDocument(
                any(Image.class)
        )).thenThrow(RateLimitException.class);

        when(outboxMapper.toEntity(
                any(OutboxDocumentResponse.class)
        )).thenReturn(outboxEntity);

        service.process(inbox);

        assertEquals(
                ProcessingStatus.ALL_RETRY_FAILED,
                inbox.getStatus()
        );

        assertEquals(
                (short) 1,
                inbox.getRetryCount()
        );

        assertNotNull(inbox.getProcessedAt());

        ArgumentCaptor<OutboxDocumentResponse> outboxCaptor =
                ArgumentCaptor.forClass(
                        OutboxDocumentResponse.class
                );

        verify(outboxMapper).toEntity(outboxCaptor.capture());

        OutboxDocumentResponse failureOutbox =
                outboxCaptor.getValue();

        assertEquals(
                eventId,
                failureOutbox.getEventId()
        );

        assertNotNull(
                failureOutbox.getResponseEventId()
        );

        assertNotEquals(
                eventId,
                failureOutbox.getResponseEventId()
        );

        assertEquals(
                documentId,
                failureOutbox.getDocumentId()
        );

        assertEquals(
                patientId,
                failureOutbox.getPatientId()
        );

        assertEquals(
                ProcessingStatus.PENDING,
                failureOutbox.getStatus()
        );

        assertEquals(
                ProcessingStatus.FAILED,
                failureOutbox.getResponseStatus()
        );

        assertTrue(
                failureOutbox.getDocuments().isEmpty()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                failureOutbox.getErrorCode()
        );

        assertEquals(
                "O limite de uso do serviço de inteligência "
                        + "artificial foi excedido.",
                failureOutbox.getErrorMessage()
        );

        assertFalse(
                failureOutbox.getErrorRetryable()
        );

        assertNotNull(
                failureOutbox.getOccurredAt()
        );

        assertTrue(
                failureOutbox.getErrorDetail()
                        .startsWith("AI_QUOTA_EXCEEDED:")
        );

        ArgumentCaptor<Image> imageCaptor =
                ArgumentCaptor.forClass(Image.class);

        verify(classifyDocumentIAService)
                .classifyDocument(imageCaptor.capture());

        Image image = imageCaptor.getValue();

        assertEquals(
                CONTENT_TYPE,
                image.mimeType()
        );

        verify(outboxRepository)
                .persistOrUpdate(outboxEntity);

        verify(
                inboxRepository,
                times(2)
        ).persistOrUpdate(inboxEntity);

        verify(nextcloudStorageService)
                .load(FILE_PATH);

        verifyNoInteractions(
                documentExtractDataIAStrategy,
                documentoRepository,
                documentoMapper,
                objectIdMapper
        );
    }
}
