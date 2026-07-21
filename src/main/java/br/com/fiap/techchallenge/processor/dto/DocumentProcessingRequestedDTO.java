package br.com.fiap.techchallenge.processor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentProcessingRequestedDTO(
        Integer schemaVersion,
        String eventType,
        Instant occurredAt,
        UUID eventId,
        UUID documentId,
        UUID patientId,
        String fileUrl
) {
}
