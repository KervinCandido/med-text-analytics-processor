package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;

import java.util.UUID;

public record DocumentProcessedResponseDTO(UUID eventId, UUID documentId, UUID patientId, DocumentEntity document) {}

