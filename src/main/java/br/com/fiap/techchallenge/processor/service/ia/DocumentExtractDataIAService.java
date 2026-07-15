package br.com.fiap.techchallenge.processor.service.ia;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;
import dev.langchain4j.data.image.Image;

public interface DocumentExtractDataIAService {
    DocumentEntity extractData(Image image);
}
