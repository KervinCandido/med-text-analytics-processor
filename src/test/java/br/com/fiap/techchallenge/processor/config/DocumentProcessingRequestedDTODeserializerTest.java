package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentProcessingRequestedDTODeserializerTest {

    private static final String TOPIC =
            "document-processing-requested";

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

    private final DocumentProcessingRequestedDTODeserializer
            deserializer =
            new DocumentProcessingRequestedDTODeserializer(
                    new ObjectMapper()
                            .disable(
                                    DeserializationFeature
                                            .FAIL_ON_UNKNOWN_PROPERTIES
                            )
                            .registerModule(
                                    new JavaTimeModule()
                            )
            );

    @Test
    void shouldDeserializeVersionOneMessage() {
        String json = """
                {
                  "schemaVersion": 1,
                  "eventType": "DOCUMENT_PROCESSING_REQUESTED",
                  "eventId": "a55c53fa-f3c4-4552-a98f-7d88d16f40e2",
                  "occurredAt": "2026-07-21T13:30:15.123Z",
                  "documentId": "60ebc948-f701-462f-bffd-9167eb77b193",
                  "patientId": "240e349a-e621-44d3-b569-870bf5825939",
                  "fileUrl": "https://patient-document-service:8443/documents/60ebc948-f701-462f-bffd-9167eb77b193/file",
                  "contentType": "image/png"
                }
                """;

        DocumentProcessingRequestedDTO message =
                deserialize(json);

        assertAll(
                () -> assertEquals(
                        1,
                        message.schemaVersion()
                ),
                () -> assertEquals(
                        "DOCUMENT_PROCESSING_REQUESTED",
                        message.eventType()
                ),
                () -> assertEquals(
                        Instant.parse(
                                "2026-07-21T13:30:15.123Z"
                        ),
                        message.occurredAt()
                ),
                () -> assertEquals(
                        EVENT_ID,
                        message.eventId()
                ),
                () -> assertEquals(
                        DOCUMENT_ID,
                        message.documentId()
                ),
                () -> assertEquals(
                        PATIENT_ID,
                        message.patientId()
                ),
                () -> assertEquals(
                        FILE_URL,
                        message.fileUrl()
                ),
                () -> assertEquals(
                        CONTENT_TYPE,
                        message.contentType()
                )
        );
    }

    @Test
    void shouldRejectAdditionalFieldsEvenWhenMapperIgnoresUnknownProperties() {
        String json = """
                {
                  "schemaVersion": 1,
                  "eventType": "DOCUMENT_PROCESSING_REQUESTED",
                  "eventId": "a55c53fa-f3c4-4552-a98f-7d88d16f40e2",
                  "occurredAt": "2026-07-21T13:30:15.123Z",
                  "documentId": "60ebc948-f701-462f-bffd-9167eb77b193",
                  "patientId": "240e349a-e621-44d3-b569-870bf5825939",
                  "fileUrl": "https://patient-document-service:8443/documents/60ebc948-f701-462f-bffd-9167eb77b193/file",
                  "contentType": "image/png",
                  "futureOptionalField": "not-allowed"
                }
                """;

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> deserialize(json)
                );

        assertNotNull(
                findCause(
                        exception,
                        UnrecognizedPropertyException.class
                )
        );
    }

    private static <T extends Throwable> T findCause(
            Throwable throwable,
            Class<T> expectedType
    ) {
        Throwable current = throwable;

        while (current != null) {
            if (expectedType.isInstance(current)) {
                return expectedType.cast(current);
            }

            current = current.getCause();
        }

        return null;
    }

    private DocumentProcessingRequestedDTO deserialize(
            String json
    ) {
        return deserializer.deserialize(
                TOPIC,
                json.getBytes(StandardCharsets.UTF_8)
        );
    }
}
