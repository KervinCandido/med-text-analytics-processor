package br.com.fiap.techchallenge.processor.service.ia;

import br.com.fiap.techchallenge.processor.domain.Documento;
import dev.langchain4j.data.image.Image;

public interface DocumentExtractDataIAService {
    Documento extractData(Image image);
}
