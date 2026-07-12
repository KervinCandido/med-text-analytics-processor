package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.LipidogramaExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LipidogramaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_LIPIDOGRAMA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este lipidograma (perfil lipídico) em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"colesterolTotal\": 0.0,\n" +
                "  \"triglicerideos\": 0.0,\n" +
                "  \"colesterolHdl\": 0.0,\n" +
                "  \"colesterolLdl\": 0.0,\n" +
                "  \"colesterolVldl\": 0.0,\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Soro, Plasma)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Enzimático Automatizado, Cálculo de Friedewald)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações do perfil lipídico\"\n" +
                "}\n" +
                "Se algum valor não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        LipidogramaExame lipidograma = new LipidogramaExame();
        lipidograma.setFileName(fileName);
        lipidograma.setFilePath(storedFilePath);
        lipidograma.setUploadedAt(uploadTime);
        lipidograma.setExameTipo("LIPIDOGRAMA");
        lipidograma.setMaterial(getNullableText(dataNode, "material"));
        lipidograma.setMetodo(getNullableText(dataNode, "metodo"));
        lipidograma.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        lipidograma.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        lipidograma.setObservacoes(getList(dataNode, "observacoes"));
        lipidograma.setNotas(getList(dataNode, "notas"));
        lipidograma.setColesterolTotal(getNullableDouble(dataNode, "colesterolTotal"));
        lipidograma.setTriglicerideos(getNullableDouble(dataNode, "triglicerideos"));
        lipidograma.setColesterolHdl(getNullableDouble(dataNode, "colesterolHdl"));
        lipidograma.setColesterolLdl(getNullableDouble(dataNode, "colesterolLdl"));
        lipidograma.setColesterolVldl(getNullableDouble(dataNode, "colesterolVldl"));
        lipidograma.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        lipidograma.persist();
        return lipidograma;
    }
}
