package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class DocumentProcessedResponseDTODeserializer extends ObjectMapperDeserializer<DocumentProcessedResponseDTO> {
    public DocumentProcessedResponseDTODeserializer() {
        super(DocumentProcessedResponseDTO.class);
    }
}
