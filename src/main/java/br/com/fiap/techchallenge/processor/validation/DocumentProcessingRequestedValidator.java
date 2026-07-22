package br.com.fiap.techchallenge.processor.validation;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;

import java.net.URI;
import java.net.URISyntaxException;

public final class DocumentProcessingRequestedValidator {

    public static final int SUPPORTED_SCHEMA_VERSION = 1;

    public static final String EXPECTED_EVENT_TYPE =
            "DOCUMENT_PROCESSING_REQUESTED";

    private static final int CONTENT_TYPE_MAX_LENGTH = 100;

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

        requirePresent(
                message.schemaVersion(),
                "schemaVersion"
        );

        if (
                !Integer.valueOf(SUPPORTED_SCHEMA_VERSION)
                        .equals(message.schemaVersion())
        ) {
            throw new IllegalArgumentException(
                    "Unsupported schemaVersion: "
                            + message.schemaVersion()
            );
        }

        requireNonBlank(
                message.eventType(),
                "eventType"
        );

        if (!EXPECTED_EVENT_TYPE.equals(message.eventType())) {
            throw new IllegalArgumentException(
                    "Invalid eventType: "
                            + message.eventType()
            );
        }

        requirePresent(
                message.occurredAt(),
                "occurredAt"
        );

        requirePresent(
                message.eventId(),
                "eventId"
        );

        requirePresent(
                message.documentId(),
                "documentId"
        );

        requirePresent(
                message.patientId(),
                "patientId"
        );

        requireNonBlank(
                message.fileUrl(),
                "fileUrl"
        );

        validateAbsoluteUri(
                message.fileUrl(),
                "fileUrl"
        );

        requireNonBlank(
                message.contentType(),
                "contentType"
        );

        if (
                message.contentType().length()
                        > CONTENT_TYPE_MAX_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "Field exceeds maximum length: contentType"
            );
        }
    }

    private static void validateAbsoluteUri(
            String value,
            String field
    ) {
        try {
            URI uri = new URI(value);

            if (!uri.isAbsolute()) {
                throw new IllegalArgumentException(
                        "Invalid field: " + field
                );
            }
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(
                    "Invalid field: " + field,
                    exception
            );
        }
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

    private static void requireNonBlank(
            String value,
            String field
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Required field is missing: " + field
            );
        }
    }
}
