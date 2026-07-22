package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class DocumentProcessingRequestedDTODeserializer
        extends ObjectMapperDeserializer<DocumentProcessingRequestedDTO> {

    public DocumentProcessingRequestedDTODeserializer() {
        this(resolveObjectMapper());
    }

    DocumentProcessingRequestedDTODeserializer(
            ObjectMapper objectMapper
    ) {
        super(
                DocumentProcessingRequestedDTO.class,
                strictCopy(objectMapper)
        );
    }

    private static ObjectMapper resolveObjectMapper() {
        ObjectMapper objectMapper = null;

        ArcContainer container = Arc.container();

        if (container != null) {
            objectMapper =
                    container.instance(ObjectMapper.class)
                            .get();
        }

        if (objectMapper == null) {
            objectMapper =
                    new ObjectMapper()
                            .findAndRegisterModules();
        }

        return objectMapper;
    }

    private static ObjectMapper strictCopy(
            ObjectMapper objectMapper
    ) {
        return objectMapper.copy()
                .enable(
                        DeserializationFeature
                                .FAIL_ON_UNKNOWN_PROPERTIES
                );
    }
}
