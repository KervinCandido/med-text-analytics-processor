package br.com.fiap.techchallenge.processor.validation;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;

public final class DocumentProcessingRequestedValidator {

    public static final int SUPPORTED_SCHEMA_VERSION = 1;

    public static final String EXPECTED_EVENT_TYPE =
            "DOCUMENT_PROCESSING_REQUESTED";

    private DocumentProcessingRequestedValidator() {
    }

    public static void validate(
            DocumentProcessingRequestedDTO message
    ) {
        if (message == null) {
            throw new IllegalArgumentException(
                    "Document processing message must not be null"
            );
        }

        requirePresent(message.eventId(), "eventId");
        requirePresent(message.documentId(), "documentId");
        requirePresent(message.patientId(), "patientId");

        if (
                message.fileUrl() == null ||
                message.fileUrl().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Required field is missing: fileUrl"
            );
        }

        if (isLegacy(message)) {
            return;
        }

        if (
                !Integer.valueOf(SUPPORTED_SCHEMA_VERSION)
                        .equals(message.schemaVersion())
        ) {
            throw new IllegalArgumentException(
                    "Unsupported schemaVersion: " +
                    message.schemaVersion()
            );
        }

        if (!EXPECTED_EVENT_TYPE.equals(message.eventType())) {
            throw new IllegalArgumentException(
                    "Invalid eventType: " +
                    message.eventType()
            );
        }

        if (message.occurredAt() == null) {
            throw new IllegalArgumentException(
                    "Required field is missing: occurredAt"
            );
        }
    }

    public static boolean isLegacy(
            DocumentProcessingRequestedDTO message
    ) {
        return message.schemaVersion() == null
                && message.eventType() == null
                && message.occurredAt() == null;
    }

    private static void requirePresent(
            Object value,
            String field
    ) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Required field is missing: " + field
            );
        }
    }
}
