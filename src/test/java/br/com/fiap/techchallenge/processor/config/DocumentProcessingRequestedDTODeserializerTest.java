package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    private final DocumentProcessingRequestedDTODeserializer
            deserializer =
            new DocumentProcessingRequestedDTODeserializer(
                    new ObjectMapper()
                            .registerModule(
                                    new JavaTimeModule()
                            )
            );

    @Test
    void shouldDeserializeLegacyMessage() {
        String json = """
                {
                  "eventId": "a55c53fa-f3c4-4552-a98f-7d88d16f40e2",
                  "documentId": "60ebc948-f701-462f-bffd-9167eb77b193",
                  "patientId": "240e349a-e621-44d3-b569-870bf5825939",
                  "fileUrl": "https://patient-document-service:8443/documents/60ebc948-f701-462f-bffd-9167eb77b193/file"
                }
                """;

        DocumentProcessingRequestedDTO message =
                deserialize(json);

        assertAll(
                () -> assertNull(message.schemaVersion()),
                () -> assertNull(message.eventType()),
                () -> assertNull(message.occurredAt()),
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
                        "https://patient-document-service:8443/"
                                + "documents/"
                                + DOCUMENT_ID
                                + "/file",
                        message.fileUrl()
                )
        );
    }

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
                  "fileUrl": "https://patient-document-service:8443/documents/60ebc948-f701-462f-bffd-9167eb77b193/file"
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
                )
        );
    }

    @Test
    void shouldIgnoreAdditionalCompatibleFields() {
        String json = """
                {
                  "schemaVersion": 1,
                  "eventType": "DOCUMENT_PROCESSING_REQUESTED",
                  "eventId": "a55c53fa-f3c4-4552-a98f-7d88d16f40e2",
                  "occurredAt": "2026-07-21T13:30:15.123Z",
                  "documentId": "60ebc948-f701-462f-bffd-9167eb77b193",
                  "patientId": "240e349a-e621-44d3-b569-870bf5825939",
                  "fileUrl": "https://patient-document-service:8443/documents/60ebc948-f701-462f-bffd-9167eb77b193/file",
                  "futureOptionalField": "ignored"
                }
                """;

        DocumentProcessingRequestedDTO message =
                deserialize(json);

        assertEquals(
                EVENT_ID,
                message.eventId()
        );
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
