package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.Documento;
import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingCompletedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingFailedDTO;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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

    @Mock
    private DocumentProcessingResultAssembler
            resultAssembler;

    private ProcessOutboxEventService service;

    @BeforeEach
    void setUp() {
        service = new ProcessOutboxEventService(
                publisher,
                documentMapper,
                documentoRepository,
                outboxRepository,
                outboxMapper,
                resultAssembler
        );
    }

    @Test
    void shouldPublishSingleAggregatedFailure() {
        UUID correlationId = UUID.randomUUID();

        OutboxDocumentResponse outboxEvent =
                failedOutbox(correlationId);

        outboxEvent.setResponseEventId(null);

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        DocumentProcessingFailedDTO response =
                mock(DocumentProcessingFailedDTO.class);

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        when(resultAssembler.failed(outboxEvent))
                .thenReturn(response);

        service.process(outboxEvent);

        assertNotNull(
                outboxEvent.getResponseEventId()
        );

        assertNotEquals(
                correlationId,
                outboxEvent.getResponseEventId()
        );

        assertEquals(
                ProcessingStatus.PROCESSED,
                outboxEvent.getStatus()
        );

        verify(resultAssembler).failed(outboxEvent);
        verify(publisher).publish(response);

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
    void shouldPublishOneResponseWithAllDocuments() {
        UUID correlationId = UUID.randomUUID();

        ObjectId firstResultId = new ObjectId();
        ObjectId secondResultId = new ObjectId();

        OutboxDocumentResponse outboxEvent =
                successfulOutbox(
                        correlationId,
                        firstResultId,
                        secondResultId
                );

        outboxEvent.setResponseEventId(null);

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        entity.setDocuments(
                List.of(
                        firstResultId,
                        secondResultId
                )
        );

        DocumentoEntity firstEntity =
                mock(DocumentoEntity.class);

        DocumentoEntity secondEntity =
                mock(DocumentoEntity.class);

        Documento firstDocument =
                mock(Documento.class);

        Documento secondDocument =
                mock(Documento.class);

        DocumentProcessingCompletedDTO response =
                mock(DocumentProcessingCompletedDTO.class);

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        when(
                documentoRepository.buscaDocumentosPorIds(
                        entity.getDocuments()
                )
        ).thenReturn(
                List.of(
                        firstEntity,
                        secondEntity
                )
        );

        when(documentMapper.toDomain(firstEntity))
                .thenReturn(firstDocument);

        when(documentMapper.toDomain(secondEntity))
                .thenReturn(secondDocument);

        when(
                resultAssembler.completed(
                        outboxEvent,
                        List.of(
                                firstDocument,
                                secondDocument
                        )
                )
        ).thenReturn(response);

        service.process(outboxEvent);

        assertNotNull(
                outboxEvent.getResponseEventId()
        );

        assertNotEquals(
                correlationId,
                outboxEvent.getResponseEventId()
        );

        assertEquals(
                ProcessingStatus.PROCESSED,
                outboxEvent.getStatus()
        );

        verify(resultAssembler).completed(
                outboxEvent,
                List.of(
                        firstDocument,
                        secondDocument
                )
        );

        verify(publisher).publish(response);

        verify(
                outboxRepository,
                times(2)
        ).persistOrUpdate(entity);
    }

    @Test
    void shouldNotMarkOutboxProcessedWhenPublicationFails() {
        OutboxDocumentResponse outboxEvent =
                failedOutbox(UUID.randomUUID());

        OutboxDocumentResponseEntity entity =
                new OutboxDocumentResponseEntity();

        DocumentProcessingFailedDTO response =
                mock(DocumentProcessingFailedDTO.class);

        when(outboxMapper.toEntity(outboxEvent))
                .thenReturn(entity);

        when(resultAssembler.failed(outboxEvent))
                .thenReturn(response);

        doThrow(
                new CompletionException(
                        new IllegalStateException(
                                "Kafka unavailable"
                        )
                )
        ).when(publisher).publish(response);

        assertThrows(
                CompletionException.class,
                () -> service.process(outboxEvent)
        );

        assertEquals(
                ProcessingStatus.PROCESSING,
                outboxEvent.getStatus()
        );

        verify(outboxRepository)
                .persistOrUpdate(entity);

        verify(resultAssembler).failed(outboxEvent);
        verify(publisher).publish(response);

        verifyNoInteractions(
                documentoRepository,
                documentMapper
        );
    }

    private static OutboxDocumentResponse failedOutbox(
            UUID correlationId
    ) {
        OutboxDocumentResponse outbox =
                baseOutbox(correlationId);

        outbox.markFailedResponse(
                "AI_PROCESSING_ERROR",
                "Não foi possível processar "
                        + "o documento.",
                true
        );

        return outbox;
    }

    private static OutboxDocumentResponse successfulOutbox(
            UUID correlationId,
            ObjectId firstResultId,
            ObjectId secondResultId
    ) {
        OutboxDocumentResponse outbox =
                baseOutbox(correlationId);

        outbox.addDocumentId(
                firstResultId.toHexString()
        );

        outbox.addDocumentId(
                secondResultId.toHexString()
        );

        outbox.markSuccessfulResponse();

        return outbox;
    }

    private static OutboxDocumentResponse baseOutbox(
            UUID correlationId
    ) {
        OutboxDocumentResponse outbox =
                new OutboxDocumentResponse();

        outbox.setEventId(correlationId);
        outbox.setDocumentId(UUID.randomUUID());
        outbox.setPatientId(UUID.randomUUID());

        return outbox;
    }
}
