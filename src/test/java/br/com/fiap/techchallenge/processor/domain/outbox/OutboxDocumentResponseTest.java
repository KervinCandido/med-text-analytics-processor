package br.com.fiap.techchallenge.processor.domain.outbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OutboxDocumentResponseTest {

    @Test
    void shouldConvertLegacyFailureToStructuredError() {
        LocalDateTime createdAt =
                LocalDateTime.of(
                        2026,
                        7,
                        21,
                        20,
                        30
                );

        OutboxDocumentResponse outbox =
                new OutboxDocumentResponse();

        outbox.setCreatedAt(createdAt);
        outbox.setOccurredAt(null);
        outbox.setResponseStatus(
                ProcessingStatus.FAILED
        );

        outbox.setErrorDetail(
                "AI_QUOTA_EXCEEDED: "
                        + "detalhe interno que não deve "
                        + "ser publicado"
        );

        outbox.ensureOccurredAt();

        Instant occurredAt =
                outbox.getOccurredAt();

        outbox.ensureStructuredError();
        outbox.ensureOccurredAt();

        assertEquals(
                createdAt
                        .atZone(Constants.SAO_PAULO_ZONE_ID)
                        .toInstant(),
                occurredAt
        );

        assertEquals(
                occurredAt,
                outbox.getOccurredAt()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                outbox.getErrorCode()
        );

        assertEquals(
                "O limite de uso do serviço de inteligência "
                        + "artificial foi excedido.",
                outbox.getErrorMessage()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED: "
                        + "O limite de uso do serviço de "
                        + "inteligência artificial foi excedido.",
                outbox.getErrorDetail()
        );

        assertFalse(outbox.getErrorRetryable());
    }

    @Test
    void shouldGenerateStableResponseEventIdForLegacyOutbox() {
        UUID correlationId = UUID.randomUUID();

        OutboxDocumentResponse outbox =
                new OutboxDocumentResponse();

        outbox.setEventId(correlationId);
        outbox.setResponseEventId(null);

        outbox.ensureResponseEventId();

        UUID responseEventId =
                outbox.getResponseEventId();

        assertNotNull(responseEventId);

        assertNotEquals(
                correlationId,
                responseEventId
        );

        outbox.ensureResponseEventId();

        assertEquals(
                responseEventId,
                outbox.getResponseEventId()
        );
    }

    @Test
    void shouldKeepOccurredAtStableWhenResponseIsMarkedAgain() {
        OutboxDocumentResponse outbox =
                new OutboxDocumentResponse();

        outbox.markSuccessfulResponse();

        Instant firstOccurredAt =
                outbox.getOccurredAt();

        assertNotNull(firstOccurredAt);

        outbox.markSuccessfulResponse();

        assertEquals(
                firstOccurredAt,
                outbox.getOccurredAt()
        );
    }
}
