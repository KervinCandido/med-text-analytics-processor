package br.com.fiap.techchallenge.processor.service.ia;

import br.com.fiap.techchallenge.processor.domain.Document;
import dev.langchain4j.data.image.Image;

public interface DocumentExtractDataIAService {
    Document extractData(Image image);
}
