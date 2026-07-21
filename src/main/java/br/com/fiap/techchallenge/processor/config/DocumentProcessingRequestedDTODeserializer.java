package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class DocumentProcessingRequestedDTODeserializer
        extends ObjectMapperDeserializer<DocumentProcessingRequestedDTO> {

    public DocumentProcessingRequestedDTODeserializer() {
        super(DocumentProcessingRequestedDTO.class);
    }

    DocumentProcessingRequestedDTODeserializer(
            ObjectMapper objectMapper
    ) {
        super(
                DocumentProcessingRequestedDTO.class,
                objectMapper
        );
    }
}
