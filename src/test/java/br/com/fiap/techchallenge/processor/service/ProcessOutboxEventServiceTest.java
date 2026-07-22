package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.Documento;
import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxDocumentResponseEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
import br.com.fiap.techchallenge.processor.publisher.DocumentProcessedPublisher;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessOutboxEventServiceTest {

    @Mock
    private DocumentProcessedPublisher publisher;

    @Mock
    private DocumentoMapper documentMapper;

    @Mock
    private DocumentoRepository documentoRepository;

    @Mock
    private OutboxDocumentProcessedResponseRepository
            outboxRepository;

    @Mock
    private OutboxDocumentResponseMapper outboxMapper;

    private ProcessOutboxEventService service;

    @BeforeEach
    void setUp() {
        service = new ProcessOutboxEventService(
                publisher,
                documentMapper,
                documentoRepository,
                outboxRepository,
                outboxMapper
        );
    }

    @Test
    void shouldPublishStructuredVersionedFailure() {
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        OutboxDocumentResponse outboxEvent =
                new OutboxDocumentResponse();

        outboxEvent.setEventId(eventId);
        outboxEvent.setDocumentId(documentId);
        outboxEvent.setPatientId(patientId);

        outboxEvent.markFailedResponse(
                "AI_QUOTA_EXCEEDED",
                "O limite da IA foi excedido.",
                false
        );

        Instant occurredAt =
                outboxEvent.getOccurredAt();

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        service.process(outboxEvent);

        ArgumentCaptor<DocumentProcessedResponseDTO> captor =
                ArgumentCaptor.forClass(
                        DocumentProcessedResponseDTO.class
                );

        verify(publisher).publish(captor.capture());

        DocumentProcessedResponseDTO published =
                captor.getValue();

        assertEquals(1, published.schemaVersion());

        assertEquals(
                "DOCUMENT_PROCESSED_RESPONSE",
                published.eventType()
        );

        assertEquals(eventId, published.eventId());
        assertEquals(occurredAt, published.occurredAt());
        assertEquals(documentId, published.documentId());
        assertEquals(patientId, published.patientId());
        assertEquals("FAILED", published.status());

        assertNull(published.document());

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                published.error().code()
        );

        assertEquals(
                "O limite da IA foi excedido.",
                published.error().message()
        );

        assertEquals(
                false,
                published.error().retryable()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED: "
                        + "O limite da IA foi excedido.",
                published.errorDetail()
        );

        assertEquals(
                ProcessingStatus.PROCESSED,
                outboxEvent.getStatus()
        );

        verify(
                outboxRepository,
                times(2)
        ).persistOrUpdate(entity);

        verifyNoInteractions(
                documentoRepository,
                documentMapper
        );
    }

    @Test
    void shouldPublishVersionedSuccessfulResponse() {
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        OutboxDocumentResponse outboxEvent =
                new OutboxDocumentResponse();

        outboxEvent.setEventId(eventId);
        outboxEvent.setDocumentId(documentId);
        outboxEvent.setPatientId(patientId);
        outboxEvent.addDocumentId(
                new ObjectId().toHexString()
        );
        outboxEvent.markSuccessfulResponse();

        Instant occurredAt =
                outboxEvent.getOccurredAt();

        ObjectId resultId = new ObjectId();

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        entity.setDocuments(List.of(resultId));

        DocumentoEntity documentEntity =
                org.mockito.Mockito.mock(
                        DocumentoEntity.class
                );

        Documento document = org.mockito.Mockito.mock(
                Documento.class
        );

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        when(
                documentoRepository.buscaDocumentosPorIds(
                        entity.getDocuments()
                )
        ).thenReturn(List.of(documentEntity));

        when(documentMapper.toDomain(documentEntity))
                .thenReturn(document);

        when(document.getId())
                .thenReturn(resultId.toHexString());

        service.process(outboxEvent);

        ArgumentCaptor<DocumentProcessedResponseDTO> captor =
                ArgumentCaptor.forClass(
                        DocumentProcessedResponseDTO.class
                );

        verify(publisher).publish(captor.capture());

        DocumentProcessedResponseDTO published =
                captor.getValue();

        assertEquals(1, published.schemaVersion());

        assertEquals(
                "DOCUMENT_PROCESSED_RESPONSE",
                published.eventType()
        );

        assertEquals(eventId, published.eventId());
        assertEquals(occurredAt, published.occurredAt());
        assertEquals(documentId, published.documentId());
        assertEquals(patientId, published.patientId());
        assertEquals("PROCESSED", published.status());

        assertSame(document, published.document());
        assertNull(published.error());
        assertNull(published.errorDetail());

        assertEquals(
                ProcessingStatus.PROCESSED,
                outboxEvent.getStatus()
        );

        verify(
                outboxRepository,
                times(2)
        ).persistOrUpdate(entity);
    }

    @Test
    void shouldNotMarkOutboxAsProcessedWhenPublicationFails() {
        OutboxDocumentResponse outboxEvent =
                new OutboxDocumentResponse();

        outboxEvent.setEventId(UUID.randomUUID());
        outboxEvent.setDocumentId(UUID.randomUUID());
        outboxEvent.setPatientId(UUID.randomUUID());

        outboxEvent.markFailedResponse(
                "AI_PROCESSING_ERROR",
                "Não foi possível processar o documento.",
                true
        );

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        org.mockito.Mockito.doThrow(
                new java.util.concurrent.CompletionException(
                        new IllegalStateException(
                                "Kafka unavailable"
                        )
                )
        ).when(publisher).publish(
                org.mockito.ArgumentMatchers.any(
                        DocumentProcessedResponseDTO.class
                )
        );

        org.junit.jupiter.api.Assertions.assertThrows(
                java.util.concurrent.CompletionException.class,
                () -> service.process(outboxEvent)
        );

        assertEquals(
                ProcessingStatus.PROCESSING,
                outboxEvent.getStatus()
        );

        verify(outboxRepository)
                .persistOrUpdate(entity);

        verify(publisher).publish(
                org.mockito.ArgumentMatchers.any(
                        DocumentProcessedResponseDTO.class
                )
        );

        verifyNoInteractions(
                documentoRepository,
                documentMapper
        );
    }
}
