package br.com.fiap.techchallenge.processor.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record DocumentProcessingResultItemDTO(
        String resultId,
        String documentType,
        LocalDate documentDate,
        Map<String, Object> data
) {

    public DocumentProcessingResultItemDTO {
        resultId =
                DocumentProcessingResultDTO.requireText(
                        resultId,
                        "resultId",
                        64
                );

        documentType =
                DocumentProcessingResultDTO.requireText(
                        documentType,
                        "documentType",
                        100
                );

        if (data == null) {
            throw new IllegalArgumentException(
                    "data é obrigatório."
            );
        }

        for (String key : data.keySet()) {
            if (key == null) {
                throw new IllegalArgumentException(
                        "data não pode conter chave nula."
                );
            }
        }

        data = Collections.unmodifiableMap(
                new LinkedHashMap<>(data)
        );
    }
}
