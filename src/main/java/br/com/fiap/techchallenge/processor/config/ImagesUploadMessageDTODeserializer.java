package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentsUploadMessageDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ImagesUploadMessageDTODeserializer extends ObjectMapperDeserializer<DocumentsUploadMessageDTO> {
    public ImagesUploadMessageDTODeserializer() {
        super(DocumentsUploadMessageDTO.class);
    }
}
