package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentProcessedResponseDTOSerializerTest {

    private static final String TOPIC =
            "document-processed-response";

    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .disable(
                            com.fasterxml.jackson.databind
                                    .SerializationFeature
                                    .WRITE_DATES_AS_TIMESTAMPS
                    );

    private final DocumentProcessedResponseDTOSerializer
            serializer =
            new DocumentProcessedResponseDTOSerializer(
                    objectMapper
            );

    @Test
    void shouldSerializeProcessedResponseAccordingToContract()
            throws Exception {

        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Instant occurredAt =
                Instant.parse(
                        "2026-07-21T23:30:15.123Z"
                );

        Outros document = new Outros();
        document.setId("507f1f77bcf86cd799439011");
        document.setPatientId(patientId);

        DocumentProcessedResponseDTO response =
                DocumentProcessedResponseDTO.processed(
                        eventId,
                        occurredAt,
                        documentId,
                        patientId,
                        document
                );

        byte[] serialized =
                serializer.serialize(TOPIC, response);

        JsonNode root =
                objectMapper.readTree(serialized);

        assertEquals(10, root.size());

        assertEquals(
                1,
                root.get("schemaVersion").asInt()
        );

        assertEquals(
                "DOCUMENT_PROCESSED_RESPONSE",
                root.get("eventType").asText()
        );

        assertEquals(
                eventId.toString(),
                root.get("eventId").asText()
        );

        assertEquals(
                occurredAt.toString(),
                root.get("occurredAt").asText()
        );

        assertEquals(
                documentId.toString(),
                root.get("documentId").asText()
        );

        assertEquals(
                patientId.toString(),
                root.get("patientId").asText()
        );

        assertEquals(
                "PROCESSED",
                root.get("status").asText()
        );

        assertEquals(
                document.getId(),
                root.get("document")
                        .get("id")
                        .asText()
        );

        assertTrue(root.has("error"));
        assertTrue(root.get("error").isNull());

        assertTrue(root.has("errorDetail"));
        assertTrue(root.get("errorDetail").isNull());
    }

    @Test
    void shouldSerializeFailedResponseAccordingToContract()
            throws Exception {

        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Instant occurredAt =
                Instant.parse(
                        "2026-07-21T23:35:20Z"
                );

        String errorDetail =
                "AI_PROCESSING_ERROR: "
                        + "Não foi possível processar "
                        + "o documento.";

        DocumentProcessedResponseDTO response =
                DocumentProcessedResponseDTO.failed(
                        eventId,
                        occurredAt,
                        documentId,
                        patientId,
                        "AI_PROCESSING_ERROR",
                        "Não foi possível processar "
                                + "o documento.",
                        true,
                        errorDetail
                );

        byte[] serialized =
                serializer.serialize(TOPIC, response);

        JsonNode root =
                objectMapper.readTree(serialized);

        assertEquals(10, root.size());

        assertEquals(
                1,
                root.get("schemaVersion").asInt()
        );

        assertEquals(
                "DOCUMENT_PROCESSED_RESPONSE",
                root.get("eventType").asText()
        );

        assertEquals(
                occurredAt.toString(),
                root.get("occurredAt").asText()
        );

        assertEquals(
                "FAILED",
                root.get("status").asText()
        );

        assertTrue(root.has("document"));
        assertTrue(root.get("document").isNull());

        JsonNode error = root.get("error");

        assertEquals(3, error.size());

        assertEquals(
                "AI_PROCESSING_ERROR",
                error.get("code").asText()
        );

        assertEquals(
                "Não foi possível processar o documento.",
                error.get("message").asText()
        );

        assertTrue(
                error.get("retryable").asBoolean()
        );

        assertEquals(
                errorDetail,
                root.get("errorDetail").asText()
        );
    }
}
