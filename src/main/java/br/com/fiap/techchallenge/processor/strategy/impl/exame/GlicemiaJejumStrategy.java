package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.GlicemiaJejumExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class GlicemiaJejumStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_GLICEMIA_JEJUM";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame de glicemia de jejum em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"glicose\": 0.0,\n" +
                "  \"valoresReferencia\": \"Texto descrevendo os parâmetros de referência (ex: Normal: < 100 mg/dL...)\",\n" +
                "  \"tempoJejum\": \"Tempo de jejum relatado pelo paciente (ex: 8 horas, 12 horas)\",\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Soro, Plasma)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Enzimático, Hexoquinase)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame de glicemia\"\n" +
                "}\n" +
                "Se algum valor não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        GlicemiaJejumExame glicemia = new GlicemiaJejumExame();
        glicemia.setFileName(fileName);
        glicemia.setFilePath(storedFilePath);
        glicemia.setUploadedAt(uploadTime);
        glicemia.setExameTipo("GLICEMIA_JEJUM");
        glicemia.setMaterial(getNullableText(dataNode, "material"));
        glicemia.setMetodo(getNullableText(dataNode, "metodo"));
        glicemia.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        glicemia.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        glicemia.setObservacoes(getList(dataNode, "observacoes"));
        glicemia.setNotas(getList(dataNode, "notas"));
        glicemia.setGlicose(getNullableDouble(dataNode, "glicose"));
        glicemia.setValoresReferencia(getNullableText(dataNode, "valoresReferencia"));
        glicemia.setTempoJejum(getNullableText(dataNode, "tempoJejum"));
        glicemia.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        glicemia.persist();
        return glicemia;
    }
}
