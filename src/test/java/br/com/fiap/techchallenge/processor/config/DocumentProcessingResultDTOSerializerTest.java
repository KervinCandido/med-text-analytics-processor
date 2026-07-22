package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingCompletedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingErrorDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingFailedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultItemDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentProcessingResultDTOSerializerTest {

    private static final String TOPIC =
            "document-processing-result";

    private final ObjectMapper objectMapper =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .disable(
                            SerializationFeature
                                    .WRITE_DATES_AS_TIMESTAMPS
                    );

    private final DocumentProcessingResultDTOSerializer
            serializer =
            new DocumentProcessingResultDTOSerializer(
                    objectMapper
            );

    @Test
    void shouldSerializeCompletedResultAccordingToContract()
            throws Exception {

        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        DocumentProcessingCompletedDTO result =
                new DocumentProcessingCompletedDTO(
                        1,
                        "DOCUMENT_PROCESSING_COMPLETED",
                        eventId,
                        correlationId,
                        Instant.parse(
                                "2026-07-22T18:30:00Z"
                        ),
                        documentId,
                        patientId,
                        "Documento contendo hemograma "
                                + "e lipidograma.",
                        "EXAME_LABORATORIAL",
                        null,
                        null,
                        null,
                        List.of(
                                new DocumentProcessingResultItemDTO(
                                        "result-1",
                                        "EXAME_HEMOGRAMA",
                                        LocalDate.of(
                                                2026,
                                                6,
                                                10
                                        ),
                                        Map.of(
                                                "hemoglobina",
                                                14.2
                                        )
                                ),
                                new DocumentProcessingResultItemDTO(
                                        "result-2",
                                        "EXAME_LIPIDOGRAMA",
                                        LocalDate.of(
                                                2026,
                                                6,
                                                10
                                        ),
                                        Map.of(
                                                "colesterolTotal",
                                                178
                                        )
                                )
                        )
                );

        byte[] payload =
                serializer.serialize(
                        TOPIC,
                        result
                );

        JsonNode json =
                objectMapper.readTree(payload);

        assertEquals(13, json.size());
        assertEquals(1, json.get("schemaVersion").asInt());

        assertEquals(
                "DOCUMENT_PROCESSING_COMPLETED",
                json.get("eventType").asText()
        );

        assertEquals(
                eventId.toString(),
                json.get("eventId").asText()
        );

        assertEquals(
                correlationId.toString(),
                json.get("correlationId").asText()
        );

        assertEquals(
                documentId.toString(),
                json.get("documentId").asText()
        );

        assertEquals(
                patientId.toString(),
                json.get("patientId").asText()
        );

        assertTrue(json.has("specialty"));
        assertTrue(json.get("specialty").isNull());

        assertTrue(json.has("documentDate"));
        assertTrue(json.get("documentDate").isNull());

        assertTrue(json.has("confidence"));
        assertTrue(json.get("confidence").isNull());

        assertEquals(2, json.get("results").size());

        assertEquals(
                "EXAME_HEMOGRAMA",
                json.get("results")
                        .get(0)
                        .get("documentType")
                        .asText()
        );

        assertFalse(json.has("error"));
        assertFalse(json.has("status"));
        assertFalse(json.has("document"));
        assertFalse(json.has("errorDetail"));
    }

    @Test
    void shouldSerializeFailedResultWithoutSuccessFields()
            throws Exception {

        DocumentProcessingFailedDTO result =
                new DocumentProcessingFailedDTO(
                        1,
                        "DOCUMENT_PROCESSING_FAILED",
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        Instant.parse(
                                "2026-07-22T18:30:00Z"
                        ),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new DocumentProcessingErrorDTO(
                                "AI_QUOTA_EXCEEDED",
                                "O limite de uso da IA foi excedido.",
                                false
                        )
                );

        byte[] payload =
                serializer.serialize(
                        TOPIC,
                        result
                );

        JsonNode json =
                objectMapper.readTree(payload);

        assertEquals(8, json.size());

        assertEquals(
                "DOCUMENT_PROCESSING_FAILED",
                json.get("eventType").asText()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                json.get("error")
                        .get("code")
                        .asText()
        );

        assertFalse(
                json.get("error")
                        .get("retryable")
                        .asBoolean()
        );

        assertEquals(3, json.get("error").size());

        assertFalse(json.has("summary"));
        assertFalse(json.has("primaryDocumentType"));
        assertFalse(json.has("specialty"));
        assertFalse(json.has("documentDate"));
        assertFalse(json.has("confidence"));
        assertFalse(json.has("results"));

        assertFalse(json.has("status"));
        assertFalse(json.has("document"));
        assertFalse(json.has("errorDetail"));
    }
}
