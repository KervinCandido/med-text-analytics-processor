package br.com.fiap.techchallenge.processor.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record DocumentProcessingCompletedDTO(
        int schemaVersion,
        String eventType,
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID documentId,
        UUID patientId,
        String summary,
        String primaryDocumentType,
        String specialty,
        LocalDate documentDate,
        Double confidence,
        List<DocumentProcessingResultItemDTO> results
) implements DocumentProcessingResultDTO {

    public DocumentProcessingCompletedDTO {
        DocumentProcessingResultDTO.validateCommon(
                schemaVersion,
                eventType,
                DOCUMENT_PROCESSING_COMPLETED,
                eventId,
                correlationId,
                occurredAt,
                documentId,
                patientId
        );

        summary =
                DocumentProcessingResultDTO.requireText(
                        summary,
                        "summary",
                        4000
                );

        primaryDocumentType =
                DocumentProcessingResultDTO.requireText(
                        primaryDocumentType,
                        "primaryDocumentType",
                        100
                );

        specialty =
                DocumentProcessingResultDTO
                        .validateNullableText(
                                specialty,
                                "specialty",
                                100
                        );

        if (confidence != null
                && (
                !Double.isFinite(confidence)
                        || confidence < 0
                        || confidence > 1
        )) {
            throw new IllegalArgumentException(
                    "confidence deve estar entre 0 e 1."
            );
        }

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException(
                    "results deve possuir ao menos um item."
            );
        }

        Set<String> resultIds = new HashSet<>();

        for (DocumentProcessingResultItemDTO result : results) {
            if (result == null) {
                throw new IllegalArgumentException(
                        "results não pode conter item nulo."
                );
            }

            if (!resultIds.add(result.resultId())) {
                throw new IllegalArgumentException(
                        "results não pode conter resultId duplicado."
                );
            }
        }

        results = List.copyOf(results);
    }
}
