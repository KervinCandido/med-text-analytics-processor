package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import br.com.fiap.techchallenge.processor.persistence.DocumentoRepository;
import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
import br.com.fiap.techchallenge.processor.publisher.DocumentProcessedPublisher;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.List;

@ApplicationScoped
public class ProcessOutboxEventService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    ProcessOutboxEventService.class
            );

    private final DocumentProcessedPublisher
            documentProcessedPublisher;

    private final DocumentoMapper documentMapper;
    private final DocumentoRepository documentoRepository;

    private final OutboxDocumentProcessedResponseRepository
            outboxRepository;

    private final OutboxDocumentResponseMapper outboxMapper;

    @Inject
    public ProcessOutboxEventService(
            DocumentProcessedPublisher documentProcessedPublisher,
            DocumentoMapper documentMapper,
            DocumentoRepository documentoRepository,
            OutboxDocumentProcessedResponseRepository outboxRepository,
            OutboxDocumentResponseMapper outboxMapper
    ) {
        this.documentProcessedPublisher =
                documentProcessedPublisher;

        this.documentMapper = documentMapper;
        this.documentoRepository = documentoRepository;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
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
    @Fallback(fallbackMethod = "processFallback")
    @ExponentialBackoff
    public void process(
            OutboxDocumentResponse outboxEvent
    ) {
        logger.info(
                "action=processOutboxEvent, "
                        + "outboxEventId={}, eventId={}, "
                        + "documentId={}",
                outboxEvent.getOutboxId(),
                outboxEvent.getEventId(),
                outboxEvent.getDocumentId()
        );

        outboxEvent.processing();
        outboxEvent.ensureOccurredAt();

        if (outboxEvent.isFailedResponse()) {
            outboxEvent.ensureStructuredError();
        }

        var outboxEntity =
                outboxMapper.toEntity(outboxEvent);

        outboxRepository.persistOrUpdate(outboxEntity);

        if (outboxEvent.isFailedResponse()) {
            publishFailedResponse(outboxEvent);
        } else {
            publishSuccessfulResponses(
                    outboxEvent,
                    outboxEntity
            );
        }

        /*
         * Este estado só é alcançado depois que o publisher
         * recebe a confirmação da publicação Kafka.
         */
        outboxEvent.processed();

        outboxRepository.persistOrUpdate(
                outboxMapper.toEntity(outboxEvent)
        );
    }

    private void publishFailedResponse(
            OutboxDocumentResponse outboxEvent
    ) {
        DocumentProcessedResponseDTO response =
                DocumentProcessedResponseDTO.failed(
                        outboxEvent.getEventId(),
                        outboxEvent.getOccurredAt(),
                        outboxEvent.getDocumentId(),
                        outboxEvent.getPatientId(),
                        outboxEvent.getErrorCode(),
                        outboxEvent.getErrorMessage(),
                        Boolean.TRUE.equals(
                                outboxEvent.getErrorRetryable()
                        ),
                        outboxEvent.getErrorDetail()
                );

        documentProcessedPublisher.publish(response);
    }

    private void publishSuccessfulResponses(
            OutboxDocumentResponse outboxEvent,
            br.com.fiap.techchallenge.processor
                    .persistence.entity.outbox
                    .OutboxDocumentResponseEntity outboxEntity
    ) {
        List<DocumentoEntity> documentos =
                documentoRepository.buscaDocumentosPorIds(
                        outboxEntity.getDocuments()
                );

        documentos.stream()
                .map(documentMapper::toDomain)
                .map(document ->
                        DocumentProcessedResponseDTO.processed(
                                outboxEvent.getEventId(),
                                outboxEvent.getOccurredAt(),
                                outboxEvent.getDocumentId(),
                                outboxEvent.getPatientId(),
                                document
                        )
                )
                .forEach(
                        documentProcessedPublisher::publish
                );
    }

    public void processFallback(
            OutboxDocumentResponse outboxEvent
    ) {
        outboxEvent.failed();

        outboxRepository.persistOrUpdate(
                outboxMapper.toEntity(outboxEvent)
        );
    }
}
