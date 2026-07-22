package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private Emitter<DocumentProcessedResponseDTO> emitter;

    private DocumentProcessedPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new DocumentProcessedPublisher(emitter);
    }

    @Test
    void shouldReturnAfterKafkaAcknowledgement() {
        DocumentProcessedResponseDTO response =
                failedResponse();

        when(emitter.send(response))
                .thenReturn(
                        CompletableFuture.completedFuture(null)
                );

        assertDoesNotThrow(
                () -> publisher.publish(response)
        );

        verify(emitter).send(response);
    }

    @Test
    void shouldPropagateKafkaPublicationFailure() {
        DocumentProcessedResponseDTO response =
                failedResponse();

        CompletableFuture<Void> failedPublication =
                new CompletableFuture<>();

        failedPublication.completeExceptionally(
                new IllegalStateException(
                        "Kafka unavailable"
                )
        );

        when(emitter.send(response))
                .thenReturn(failedPublication);

        assertThrows(
                CompletionException.class,
                () -> publisher.publish(response)
        );

        verify(emitter).send(response);
    }

    private DocumentProcessedResponseDTO failedResponse() {
        return DocumentProcessedResponseDTO.failed(
                UUID.randomUUID(),
                Instant.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "AI_PROCESSING_ERROR",
                "Não foi possível processar o documento.",
                true,
                "AI_PROCESSING_ERROR: "
                        + "Não foi possível processar "
                        + "o documento."
        );
    }
}
