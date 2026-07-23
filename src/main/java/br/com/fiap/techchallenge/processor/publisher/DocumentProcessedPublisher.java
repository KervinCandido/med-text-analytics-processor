package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultDTO;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DocumentProcessedPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    DocumentProcessedPublisher.class
            );

    private final Emitter<
            Record<String, DocumentProcessingResultDTO>
            > emitter;

    @Inject
    public DocumentProcessedPublisher(
            @Channel("document-processing-result")
            Emitter<
                    Record<
                            String,
                            DocumentProcessingResultDTO
                            >
                    > emitter
    ) {
        this.emitter = emitter;
    }

    public void publish(
            DocumentProcessingResultDTO response
    ) {
        Record<String, DocumentProcessingResultDTO>
                kafkaRecord =
                Record.of(
                        response.documentId().toString(),
                        response
                );

        try {
            emitter.send(kafkaRecord)
                    .toCompletableFuture()
                    .join();

            logger.info(
                    "action=publishDocumentProcessingResultSuccess, "
                            + "schemaVersion={}, eventType={}, "
                            + "eventId={}, correlationId={}, "
                            + "documentId={}",
                    response.schemaVersion(),
                    response.eventType(),
                    response.eventId(),
                    response.correlationId(),
                    response.documentId()
            );
        } catch (RuntimeException exception) {
            Throwable cause =
                    exception.getCause() == null
                            ? exception
                            : exception.getCause();

            logger.error(
                    "action=publishDocumentProcessingResultFailed, "
                            + "schemaVersion={}, eventType={}, "
                            + "eventId={}, correlationId={}, "
                            + "documentId={}, "
                            + "exceptionType={}, causeType={}",
                    response.schemaVersion(),
                    response.eventType(),
                    response.eventId(),
                    response.correlationId(),
                    response.documentId(),
                    exception.getClass().getSimpleName(),
                    cause.getClass().getSimpleName()
            );

            throw exception;
        }
    }
}
