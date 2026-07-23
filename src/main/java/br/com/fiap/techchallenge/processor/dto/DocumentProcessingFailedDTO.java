package br.com.fiap.techchallenge.processor.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentProcessingFailedDTO(
        int schemaVersion,
        String eventType,
        UUID eventId,
        UUID correlationId,
        Instant occurredAt,
        UUID documentId,
        UUID patientId,
        DocumentProcessingErrorDTO error
) implements DocumentProcessingResultDTO {

    public DocumentProcessingFailedDTO {
        DocumentProcessingResultDTO.validateCommon(
                schemaVersion,
                eventType,
                DOCUMENT_PROCESSING_FAILED,
                eventId,
                correlationId,
                occurredAt,
                documentId,
                patientId
        );

        if (error == null) {
            throw new IllegalArgumentException(
                    "error é obrigatório para falha."
            );
        }
    }
}
