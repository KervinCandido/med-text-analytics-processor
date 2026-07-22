package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.Documento;

import java.time.Instant;
import java.util.UUID;

public record DocumentProcessedResponseDTO(
        int schemaVersion,
        String eventType,
        UUID eventId,
        Instant occurredAt,
        UUID documentId,
        UUID patientId,
        String status,
        Documento document,
        DocumentProcessingErrorDTO error,
        String errorDetail
) {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    public static final String DOCUMENT_PROCESSED_RESPONSE =
            "DOCUMENT_PROCESSED_RESPONSE";

    private static final String PROCESSED = "PROCESSED";
    private static final String FAILED = "FAILED";

    public DocumentProcessedResponseDTO {
        if (schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw new IllegalArgumentException(
                    "schemaVersion deve ser igual a 1."
            );
        }

        if (!DOCUMENT_PROCESSED_RESPONSE.equals(eventType)) {
            throw new IllegalArgumentException(
                    "eventType inválido para a resposta."
            );
        }

        requireNonNull(eventId, "eventId");
        requireNonNull(occurredAt, "occurredAt");
        requireNonNull(documentId, "documentId");
        requireNonNull(patientId, "patientId");

        if (!PROCESSED.equals(status)
                && !FAILED.equals(status)) {
            throw new IllegalArgumentException(
                    "status deve ser PROCESSED ou FAILED."
            );
        }

        if (errorDetail != null
                && errorDetail.length() > 2000) {
            throw new IllegalArgumentException(
                    "errorDetail deve possuir até 2000 caracteres."
            );
        }

        if (PROCESSED.equals(status)) {
            validateProcessedResponse(document, error);
        } else {
            validateFailedResponse(document, error);
        }
    }

    public static DocumentProcessedResponseDTO processed(
            UUID eventId,
            Instant occurredAt,
            UUID documentId,
            UUID patientId,
            Documento document
    ) {
        return new DocumentProcessedResponseDTO(
                CURRENT_SCHEMA_VERSION,
                DOCUMENT_PROCESSED_RESPONSE,
                eventId,
                occurredAt,
                documentId,
                patientId,
                PROCESSED,
                document,
                null,
                null
        );
    }

    public static DocumentProcessedResponseDTO failed(
            UUID eventId,
            Instant occurredAt,
            UUID documentId,
            UUID patientId,
            String errorCode,
            String errorMessage,
            boolean errorRetryable,
            String errorDetail
    ) {
        return new DocumentProcessedResponseDTO(
                CURRENT_SCHEMA_VERSION,
                DOCUMENT_PROCESSED_RESPONSE,
                eventId,
                occurredAt,
                documentId,
                patientId,
                FAILED,
                null,
                new DocumentProcessingErrorDTO(
                        errorCode,
                        errorMessage,
                        errorRetryable
                ),
                errorDetail
        );
    }

    private static void validateProcessedResponse(
            Documento document,
            DocumentProcessingErrorDTO error
    ) {
        if (document == null) {
            throw new IllegalArgumentException(
                    "document é obrigatório para PROCESSED."
            );
        }

        String externalResultId = document.getId();

        if (externalResultId == null
                || externalResultId.isBlank()) {
            throw new IllegalArgumentException(
                    "document.id é obrigatório para PROCESSED."
            );
        }

        if (externalResultId.length() > 64) {
            throw new IllegalArgumentException(
                    "document.id deve possuir até 64 caracteres."
            );
        }

        if (error != null) {
            throw new IllegalArgumentException(
                    "error deve ser nulo para PROCESSED."
            );
        }
    }

    private static void validateFailedResponse(
            Documento document,
            DocumentProcessingErrorDTO error
    ) {
        if (document != null) {
            throw new IllegalArgumentException(
                    "document deve ser nulo para FAILED."
            );
        }

        if (error == null) {
            throw new IllegalArgumentException(
                    "error é obrigatório para FAILED."
            );
        }
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
