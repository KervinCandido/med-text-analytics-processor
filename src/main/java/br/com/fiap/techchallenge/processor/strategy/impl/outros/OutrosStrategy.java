package br.com.fiap.techchallenge.processor.strategy.impl.outros;

import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class OutrosStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "OUTROS";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise esta imagem em português e forneça uma descrição geral da imagem no seguinte formato JSON:\n" +
                "{\n" +
                "  \"descricaoGeral\": \"Descrição geral detalhada do conteúdo da imagem.\"\n" +
                "}\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Outros outros = new Outros();
        outros.setFileName(fileName);
        outros.setFilePath(storedFilePath);
        outros.setUploadedAt(uploadTime);
        outros.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        outros.persist();
        return outros;
    }
}
