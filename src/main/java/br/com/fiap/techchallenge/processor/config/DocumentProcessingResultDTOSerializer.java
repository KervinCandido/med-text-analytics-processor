package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class DocumentProcessingResultDTOSerializer
        extends ObjectMapperSerializer<DocumentProcessingResultDTO> {

    public DocumentProcessingResultDTOSerializer() {
        super();
    }

    DocumentProcessingResultDTOSerializer(
            ObjectMapper objectMapper
    ) {
        super(objectMapper);
    }
}
