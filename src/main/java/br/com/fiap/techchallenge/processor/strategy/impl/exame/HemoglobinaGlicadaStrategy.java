package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.HemoglobinaGlicadaExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class HemoglobinaGlicadaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_HEMOGLOBINA_GLICADA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame de hemoglobina glicada (HbA1c) em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"hemoglobinaGlicada\": 0.0,\n" +
                "  \"valoresReferencia\": \"Texto descrevendo os parâmetros de referência (ex: Normal: abaixo de 5,7%...)\",\n" +
                "  \"glicemiaMediaEstimada\": 0.0,\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Sangue total EDTA)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: HPLC, Imunoensaio)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame de hemoglobina glicada\"\n" +
                "}\n" +
                "Se algum valor não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        HemoglobinaGlicadaExame glicada = new HemoglobinaGlicadaExame();
        glicada.setFileName(fileName);
        glicada.setFilePath(storedFilePath);
        glicada.setUploadedAt(uploadTime);
        glicada.setExameTipo("HEMOGLOBINA_GLICADA");
        glicada.setMaterial(getNullableText(dataNode, "material"));
        glicada.setMetodo(getNullableText(dataNode, "metodo"));
        glicada.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        glicada.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        glicada.setObservacoes(getList(dataNode, "observacoes"));
        glicada.setNotas(getList(dataNode, "notas"));
        glicada.setHemoglobinaGlicada(getNullableDouble(dataNode, "hemoglobinaGlicada"));
        glicada.setValoresReferencia(getNullableText(dataNode, "valoresReferencia"));
        glicada.setGlicemiaMediaEstimada(getNullableDouble(dataNode, "glicemiaMediaEstimada"));
        glicada.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        glicada.persist();
        return glicada;
    }
}
