package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.Document;

import java.util.UUID;

public record DocumentProcessedResponseDTO(UUID eventId, UUID documentId, UUID patientId, Document document) {}

