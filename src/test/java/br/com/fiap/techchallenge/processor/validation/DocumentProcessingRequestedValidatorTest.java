package br.com.fiap.techchallenge.processor.validation;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static final Instant OCCURRED_AT =
            Instant.parse(
                    "2026-07-21T13:30:15.123Z"
            );

    @Test
    void shouldAcceptLegacyMessage() {
        DocumentProcessingRequestedDTO message =
                legacyMessage();

        assertDoesNotThrow(
                () -> DocumentProcessingRequestedValidator
                        .validate(message)
        );

        assertTrue(
                DocumentProcessingRequestedValidator
                        .isLegacy(message)
        );
    }

    @Test
    void shouldAcceptVersionOneMessage() {
        DocumentProcessingRequestedDTO message =
                versionOneMessage();

        assertDoesNotThrow(
                () -> DocumentProcessingRequestedValidator
                        .validate(message)
        );

        assertFalse(
                DocumentProcessingRequestedValidator
                        .isLegacy(message)
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
    void shouldRejectMissingEventId() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        null,
                        null,
                        null,
                        null,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL
                );

        assertRequiredFieldError(
                message,
                "eventId"
        );
    }

    @Test
    void shouldRejectMissingDocumentId() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        null,
                        null,
                        null,
                        EVENT_ID,
                        null,
                        PATIENT_ID,
                        FILE_URL
                );

        assertRequiredFieldError(
                message,
                "documentId"
        );
    }

    @Test
    void shouldRejectMissingPatientId() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        null,
                        null,
                        null,
                        EVENT_ID,
                        DOCUMENT_ID,
                        null,
                        FILE_URL
                );

        assertRequiredFieldError(
                message,
                "patientId"
        );
    }

    @Test
    void shouldRejectBlankFileUrl() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        null,
                        null,
                        null,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        " "
                );

        assertRequiredFieldError(
                message,
                "fileUrl"
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
                        FILE_URL
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
    void shouldRejectInvalidEventType() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        1,
                        "INVALID_EVENT",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL
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
    void shouldRejectMissingOccurredAtInVersionOneMessage() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        1,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        null,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL
                );

        assertRequiredFieldError(
                message,
                "occurredAt"
        );
    }

    @Test
    void shouldRejectPartiallyVersionedMessage() {
        DocumentProcessingRequestedDTO message =
                new DocumentProcessingRequestedDTO(
                        null,
                        "DOCUMENT_PROCESSING_REQUESTED",
                        OCCURRED_AT,
                        EVENT_ID,
                        DOCUMENT_ID,
                        PATIENT_ID,
                        FILE_URL
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> DocumentProcessingRequestedValidator
                                .validate(message)
                );

        assertEquals(
                "Unsupported schemaVersion: null",
                exception.getMessage()
        );
    }

    private static DocumentProcessingRequestedDTO legacyMessage() {
        return new DocumentProcessingRequestedDTO(
                null,
                null,
                null,
                EVENT_ID,
                DOCUMENT_ID,
                PATIENT_ID,
                FILE_URL
        );
    }

    private static DocumentProcessingRequestedDTO versionOneMessage() {
        return new DocumentProcessingRequestedDTO(
                1,
                "DOCUMENT_PROCESSING_REQUESTED",
                OCCURRED_AT,
                EVENT_ID,
                DOCUMENT_ID,
                PATIENT_ID,
                FILE_URL
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
