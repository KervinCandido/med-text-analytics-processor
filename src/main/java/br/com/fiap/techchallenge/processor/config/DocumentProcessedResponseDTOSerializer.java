package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class DocumentProcessedResponseDTOSerializer
        extends ObjectMapperSerializer<DocumentProcessedResponseDTO> {

    /*
     * Utilizado pelo Kafka no runtime do Quarkus.
     */
    public DocumentProcessedResponseDTOSerializer() {
        super();
    }

    /*
     * Permite fornecer um ObjectMapper configurado em testes unitários
     * executados sem o container Arc.
     */
    DocumentProcessedResponseDTOSerializer(
            ObjectMapper objectMapper
    ) {
        super(objectMapper);
    }
}
