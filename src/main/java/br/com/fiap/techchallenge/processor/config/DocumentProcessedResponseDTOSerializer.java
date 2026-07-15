package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class DocumentProcessedResponseDTOSerializer extends ObjectMapperSerializer<DocumentProcessedResponseDTO> {}
