package br.com.fiap.techchallenge.processor.dto;

import java.util.UUID;

public record DocumentProcessingRequestedDTO(UUID eventId, UUID documentId, UUID patientId, String fileUrl) {}

