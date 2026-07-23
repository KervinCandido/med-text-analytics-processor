package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.Documento;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
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

    private final DocumentProcessingResultAssembler
            resultAssembler;

    @Inject
    public ProcessOutboxEventService(
            DocumentProcessedPublisher documentProcessedPublisher,
            DocumentoMapper documentMapper,
            DocumentoRepository documentoRepository,
            OutboxDocumentProcessedResponseRepository
                    outboxRepository,
            OutboxDocumentResponseMapper outboxMapper,
            DocumentProcessingResultAssembler resultAssembler
    ) {
        this.documentProcessedPublisher =
                documentProcessedPublisher;

        this.documentMapper = documentMapper;
        this.documentoRepository = documentoRepository;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
        this.resultAssembler = resultAssembler;
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
                        + "outboxEventId={}, responseEventId={}, "
                        + "correlationId={}, documentId={}",
                outboxEvent.getOutboxId(),
                outboxEvent.getResponseEventId(),
                outboxEvent.getEventId(),
                outboxEvent.getDocumentId()
        );

        outboxEvent.processing();
        outboxEvent.ensureResponseEventId();
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
            publishSuccessfulResponse(
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
        documentProcessedPublisher.publish(
                resultAssembler.failed(outboxEvent)
        );
    }

    private void publishSuccessfulResponse(
            OutboxDocumentResponse outboxEvent,
            br.com.fiap.techchallenge.processor
                    .persistence.entity.outbox
                    .OutboxDocumentResponseEntity outboxEntity
    ) {
        if (outboxEntity.getDocuments() == null
                || outboxEntity.getDocuments().isEmpty()) {
            throw new IllegalStateException(
                    "Outbox de sucesso sem documentos processados."
            );
        }

        List<DocumentoEntity> documentEntities =
                documentoRepository.buscaDocumentosPorIds(
                        outboxEntity.getDocuments()
                );

        if (documentEntities.size()
                != outboxEntity.getDocuments().size()) {
            throw new IllegalStateException(
                    "Nem todos os documentos processados "
                            + "foram encontrados."
            );
        }

        List<Documento> documents =
                documentEntities.stream()
                        .map(documentMapper::toDomain)
                        .toList();

        documentProcessedPublisher.publish(
                resultAssembler.completed(
                        outboxEvent,
                        documents
                )
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
