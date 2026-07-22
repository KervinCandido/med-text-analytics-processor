package br.com.fiap.techchallenge.processor.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentProcessingRequestedDTO(
        Integer schemaVersion,
        String eventType,
        Instant occurredAt,
        UUID eventId,
        UUID documentId,
        UUID patientId,
        String fileUrl,
        String contentType
) {
}
