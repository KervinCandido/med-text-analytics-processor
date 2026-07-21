package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxDocumentResponseEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
import br.com.fiap.techchallenge.processor.publisher.DocumentProcessedPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    private OutboxDocumentProcessedResponseRepository outboxRepository;

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
    void shouldPublishFailedResponseWithoutProcessedDocument() {
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        String errorDetail =
                "AI_QUOTA_EXCEEDED: limite da IA excedido.";

        OutboxDocumentResponse outboxEvent =
                new OutboxDocumentResponse();

        outboxEvent.setEventId(eventId);
        outboxEvent.setDocumentId(documentId);
        outboxEvent.setPatientId(patientId);
        outboxEvent.markFailedResponse(errorDetail);

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

        assertEquals(eventId, published.eventId());
        assertEquals(documentId, published.documentId());
        assertEquals(patientId, published.patientId());

        assertEquals(
                ProcessingStatus.FAILED.name(),
                published.status()
        );

        assertNull(published.document());
        assertEquals(errorDetail, published.errorDetail());

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
}
