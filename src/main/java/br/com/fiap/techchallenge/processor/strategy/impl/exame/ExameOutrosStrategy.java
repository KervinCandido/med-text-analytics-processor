package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.Exame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ExameOutrosStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_OUTROS";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame médico em português e extraia as informações estruturadas possíveis.\n" +
                "Retorne no seguinte formato JSON:\n" +
                "{\n" +
                "  \"material\": \"Amostra de material utilizada (se houver, senão null)\",\n" +
                "  \"metodo\": \"Metodologia de análise (se houver, senão null)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Descrição detalhada de qual é o exame e o resultado principal extraído.\"\n" +
                "}\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Exame outroExame = new Exame();
        outroExame.setFileName(fileName);
        outroExame.setFilePath(storedFilePath);
        outroExame.setUploadedAt(uploadTime);
        outroExame.setExameTipo("OUTRO");
        outroExame.setMaterial(getNullableText(dataNode, "material"));
        outroExame.setMetodo(getNullableText(dataNode, "metodo"));
        outroExame.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        outroExame.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        outroExame.setObservacoes(getList(dataNode, "observacoes"));
        outroExame.setNotas(getList(dataNode, "notas"));
        outroExame.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        outroExame.persist();
        return outroExame;
    }
}
