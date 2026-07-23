package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingErrorDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingFailedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultDTO;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentProcessedPublisherTest {

    @Mock
    private Emitter<
            Record<String, DocumentProcessingResultDTO>
            > emitter;

    private DocumentProcessedPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher =
                new DocumentProcessedPublisher(emitter);
    }

    @Test
    void shouldPublishUsingDocumentIdAsKafkaKey() {
        DocumentProcessingFailedDTO response =
                failedResponse();

        when(emitter.send(anyKafkaRecord()))
                .thenReturn(
                        CompletableFuture.completedFuture(null)
                );

        assertDoesNotThrow(
                () -> publisher.publish(response)
        );

        verify(emitter).send(
                matchingKafkaRecord(response)
        );
    }

    @Test
    void shouldPropagateKafkaPublicationFailure() {
        DocumentProcessingFailedDTO response =
                failedResponse();

        CompletableFuture<Void> failedPublication =
                new CompletableFuture<>();

        failedPublication.completeExceptionally(
                new IllegalStateException(
                        "Kafka unavailable"
                )
        );

        when(emitter.send(anyKafkaRecord()))
                .thenReturn(failedPublication);

        assertThrows(
                CompletionException.class,
                () -> publisher.publish(response)
        );

        verify(emitter).send(anyKafkaRecord());
    }

    private static Record<
            String,
            DocumentProcessingResultDTO
            > anyKafkaRecord() {

        return ArgumentMatchers.any();
    }

    private static Record<
            String,
            DocumentProcessingResultDTO
            > matchingKafkaRecord(
            DocumentProcessingResultDTO expectedResponse
    ) {
        return ArgumentMatchers.argThat(
                record ->
                        record != null
                                && expectedResponse
                                .documentId()
                                .toString()
                                .equals(record.key())
                                && expectedResponse.equals(
                                record.value()
                        )
        );
    }

    private static DocumentProcessingFailedDTO
    failedResponse() {
        return new DocumentProcessingFailedDTO(
                1,
                "DOCUMENT_PROCESSING_FAILED",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new DocumentProcessingErrorDTO(
                        "AI_PROCESSING_ERROR",
                        "Não foi possível processar "
                                + "o documento.",
                        true
                )
        );
    }
}
