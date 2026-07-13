package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class DocumentProcessingRequestedDTODeserializer extends ObjectMapperDeserializer<DocumentProcessingRequestedDTO> {
    public DocumentProcessingRequestedDTODeserializer() {
        super(DocumentProcessingRequestedDTO.class);
    }
}
