package br.com.fiap.techchallenge.processor.dto;

import java.util.UUID;

public record InboxEventDTO (UUID eventId, UUID documentId, String filePath, UUID patientId) {

    public InboxEventDTO(String eventId, String documentId, String filePaths, String patientId) {
        this(UUID.fromString(eventId), UUID.fromString(documentId), filePaths, UUID.fromString(patientId));
    }
}
