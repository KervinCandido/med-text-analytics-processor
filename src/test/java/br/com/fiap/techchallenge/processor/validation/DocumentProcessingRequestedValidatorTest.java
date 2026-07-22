package br.com.fiap.techchallenge.processor.validation;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentProcessingRequestedValidatorTest {

    private static final UUID EVENT_ID =
            UUID.fromString(
                    "a55c53fa-f3c4-4552-a98f-7d88d16f40e2"
            );

    private static final UUID DOCUMENT_ID =
            UUID.fromString(
                    "60ebc948-f701-462f-bffd-9167eb77b193"
            );

    private static final UUID PATIENT_ID =
            UUID.fromString(
                    "240e349a-e621-44d3-b569-870bf5825939"
            );

    private static final String FILE_URL =
            "https://patient-document-service:8443/documents/"
                    + DOCUMENT_ID
                    + "/file";

    private static final String CONTENT_TYPE = "image/png";

    private static final Instant OCCURRED_AT =
            Instant.parse(
                    "2026-07-21T13:30:15.123Z"
            );

    @Test
    void shouldAcceptVersionOneMessage() {
        assertDoesNotThrow(
                () -> DocumentProcessingRequestedValidator
                        .validate(validMessage())
        );
    }

    @Test
    void shouldRejectNullMessage() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(null)
                );

        assertEquals(
                "Document processing message must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMissingSchemaVersion() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        null,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "schemaVersion"
        );
    }

    @Test
    void shouldRejectUnsupportedSchemaVersion() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        2,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Unsupported schemaVersion: 2",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMissingEventType() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        null,
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "eventType"
        );
    }

    @Test
    void shouldRejectInvalidEventType() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        1,
                        "INVALID_EVENT",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Invalid eventType: INVALID_EVENT",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMissingOccurredAt() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        null,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "occurredAt"
        );
    }

    @Test
    void shouldRejectMissingEventId() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        null,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "eventId"
        );
    }

    @Test
    void shouldRejectMissingDocumentId() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        null,
                        PATIENT_ID,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "documentId"
        );
    }

    @Test
    void shouldRejectMissingPatientId() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        null,
                        FILE_URL,
                        CONTENT_TYPE
                ),
                "patientId"
        );
    }

    @Test
    void shouldRejectBlankFileUrl() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        " ",
                        CONTENT_TYPE
                ),
                "fileUrl"
        );
    }

    @Test
    void shouldRejectInvalidFileUrl() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        "not-an-absolute-uri",
                        CONTENT_TYPE
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Invalid field: fileUrl",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectMissingContentType() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        null
                ),
                "contentType"
        );
    }

    @Test
    void shouldRejectBlankContentType() {
        assertRequiredFieldError(
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        " "
                ),
                "contentType"
        );
    }

    @Test
    void shouldRejectContentTypeAboveMaximumLength() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL,
                        "a".repeat(101)
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Field exceeds maximum length: contentType",
                exception.getMessage()
        );
    }

    private static DocumentProcessingRequestedDTO validMessage() {
        return new DocumentProcessingRequestedDTO(
                1,
                "DOCUMENT_PROCESSING_REQUESTED",
                OCCURRED_AT,
                EVENT_ID,
                DOCUMENT_ID,
                PATIENT_ID,
                FILE_URL,
                CONTENT_TYPE
        );
    }

    private static void assertRequiredFieldError(
            DocumentProcessingRequestedDTO message,
            String field
    ) {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Required field is missing: " + field,
                exception.getMessage()
        );
    }
}
