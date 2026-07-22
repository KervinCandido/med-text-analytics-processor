package br.com.fiap.techchallenge.processor.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentProcessingResultDTOTest {

    @Test
    void shouldAcceptCompletedResultWithMultipleItems() {
        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        DocumentProcessingCompletedDTO result =
                completedResult(
                        eventId,
                        correlationId,
                        List.of(
                                item(
                                        "result-1",
                                        "EXAME_HEMOGRAMA"
                                ),
                                item(
                                        "result-2",
                                        "EXAME_LIPIDOGRAMA"
                                )
                        )
                );

        assertEquals(1, result.schemaVersion());

        assertEquals(
                "DOCUMENT_PROCESSING_COMPLETED",
                result.eventType()
        );

        assertEquals(eventId, result.eventId());
        assertEquals(correlationId, result.correlationId());
        assertEquals(2, result.results().size());
        assertEquals("EXAME_LABORATORIAL",
                result.primaryDocumentType());
    }

    @Test
    void shouldCreateDefensiveCopiesOfResultsAndData() {
        Map<String, Object> mutableData =
                new LinkedHashMap<>();

        mutableData.put("hemoglobina", 14.2);

        DocumentProcessingResultItemDTO item =
                new DocumentProcessingResultItemDTO(
                        "result-1",
                        "EXAME_HEMOGRAMA",
                        LocalDate.of(2026, 6, 10),
                        mutableData
                );

        List<DocumentProcessingResultItemDTO>
                mutableResults = new ArrayList<>();

        mutableResults.add(item);

        DocumentProcessingCompletedDTO result =
                completedResult(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        mutableResults
                );

        mutableData.put("leucocitos", 7200);

        mutableResults.add(
                item(
                        "result-2",
                        "EXAME_LIPIDOGRAMA"
                )
        );

        assertEquals(1, result.results().size());

        assertFalse(
                result.results()
                        .getFirst()
                        .data()
                        .containsKey("leucocitos")
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.results().add(
                        item(
                                "result-3",
                                "EXAME_GLICEMIA_JEJUM"
                        )
                )
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> result.results()
                        .getFirst()
                        .data()
                        .put("novoCampo", 1)
        );
    }

    @Test
    void shouldRejectNullDataKey() {
        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(null, "invalid");

        assertThrows(
                IllegalArgumentException.class,
                () -> new DocumentProcessingResultItemDTO(
                        "result-1",
                        "EXAME_HEMOGRAMA",
                        LocalDate.of(2026, 6, 10),
                        data
                )
        );
    }

    @Test
    void shouldRejectDuplicateResultIds() {
        List<DocumentProcessingResultItemDTO> results =
                List.of(
                        item(
                                "same-result-id",
                                "EXAME_HEMOGRAMA"
                        ),
                        item(
                                "same-result-id",
                                "EXAME_LIPIDOGRAMA"
                        )
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> completedResult(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        results
                )
        );
    }

    @Test
    void shouldRejectResultEventIdEqualToCorrelationId() {
        UUID sameId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> completedResult(
                        sameId,
                        sameId,
                        List.of(
                                item(
                                        "result-1",
                                        "EXAME_HEMOGRAMA"
                                )
                        )
                )
        );
    }

    @Test
    void shouldAcceptFailedResultWithoutSuccessFields() {
        UUID eventId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();

        DocumentProcessingFailedDTO result =
                new DocumentProcessingFailedDTO(
                        1,
                        "DOCUMENT_PROCESSING_FAILED",
                        eventId,
                        correlationId,
                        Instant.parse(
                                "2026-07-22T18:30:00Z"
                        ),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new DocumentProcessingErrorDTO(
                                "AI_QUOTA_EXCEEDED",
                                "Limite da IA excedido.",
                                false
                        )
                );

        assertEquals(eventId, result.eventId());
        assertEquals(correlationId, result.correlationId());

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                result.error().code()
        );

        assertFalse(result.error().retryable());
    }

    @Test
    void shouldEnforceOfficialErrorMessageLimit() {
        assertDoesNotThrow(
                () -> new DocumentProcessingErrorDTO(
                        "AI_PROCESSING_ERROR",
                        "a".repeat(2000),
                        true
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new DocumentProcessingErrorDTO(
                        "AI_PROCESSING_ERROR",
                        "a".repeat(2001),
                        true
                )
        );
    }

    @Test
    void shouldAcceptNullOptionalMetadata() {
        DocumentProcessingCompletedDTO result =
                completedResult(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        List.of(
                                item(
                                        "result-1",
                                        "EXAME_HEMOGRAMA"
                                )
                        )
                );

        assertEquals(null, result.specialty());
        assertEquals(null, result.documentDate());
        assertEquals(null, result.confidence());
        assertTrue(result.summary().contains("hemograma"));
    }

    private static DocumentProcessingCompletedDTO
    completedResult(
            UUID eventId,
            UUID correlationId,
            List<DocumentProcessingResultItemDTO> results
    ) {
        return new DocumentProcessingCompletedDTO(
                1,
                "DOCUMENT_PROCESSING_COMPLETED",
                eventId,
                correlationId,
                Instant.parse(
                        "2026-07-22T18:30:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Documento contendo hemograma "
                        + "e lipidograma.",
                "EXAME_LABORATORIAL",
                null,
                null,
                null,
                results
        );
    }

    private static DocumentProcessingResultItemDTO item(
            String resultId,
            String documentType
    ) {
        return new DocumentProcessingResultItemDTO(
                resultId,
                documentType,
                LocalDate.of(2026, 6, 10),
                Map.of(
                        "valor",
                        10
                )
        );
    }
}
