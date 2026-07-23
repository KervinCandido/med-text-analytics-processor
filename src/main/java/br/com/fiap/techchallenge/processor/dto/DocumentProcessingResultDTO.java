package br.com.fiap.techchallenge.processor.dto;

import java.time.Instant;
import java.util.UUID;

public interface DocumentProcessingResultDTO {

    int CURRENT_SCHEMA_VERSION = 1;

    String DOCUMENT_PROCESSING_COMPLETED =
            "DOCUMENT_PROCESSING_COMPLETED";

    String DOCUMENT_PROCESSING_FAILED =
            "DOCUMENT_PROCESSING_FAILED";

    int schemaVersion();

    String eventType();

    UUID eventId();

    UUID correlationId();

    Instant occurredAt();

    UUID documentId();

    UUID patientId();

    static void validateCommon(
            int schemaVersion,
            String eventType,
            String expectedEventType,
            UUID eventId,
            UUID correlationId,
            Instant occurredAt,
            UUID documentId,
            UUID patientId
    ) {
        if (schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException(
                    "schemaVersion deve ser igual a 1."
            );
        }

        if (!expectedEventType.equals(eventType)) {
            throw new IllegalArgumentException(
                    "eventType inválido para o resultado."
            );
        }

        requireNonNull(eventId, "eventId");
        requireNonNull(correlationId, "correlationId");
        requireNonNull(occurredAt, "occurredAt");
        requireNonNull(documentId, "documentId");
        requireNonNull(patientId, "patientId");

        if (eventId.equals(correlationId)) {
            throw new IllegalArgumentException(
                    "eventId do resultado deve ser diferente "
                            + "do correlationId."
            );
        }
    }

    static String requireText(
            String value,
            String field,
            int maximumLength
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " é obrigatório."
            );
        }

        if (value.length() > maximumLength) {
            throw new IllegalArgumentException(
                    field + " deve possuir até "
                            + maximumLength
                            + " caracteres."
            );
        }

        return value;
    }

    static String validateNullableText(
            String value,
            String field,
            int maximumLength
    ) {
        if (value == null) {
            return null;
        }

        return requireText(
                value,
                field,
                maximumLength
        );
    }

    private static void requireNonNull(
            Object value,
            String field
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    field + " é obrigatório."
            );
        }
    }
}
