package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.dto.DocumentsUploadMessageDTO;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class ImagesUploadMessageDTOSerializer extends ObjectMapperSerializer<DocumentsUploadMessageDTO> {}
