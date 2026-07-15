package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class DocumentProcessingRequestedDTOSerializer extends ObjectMapperSerializer<DocumentProcessingRequestedDTO> {}
