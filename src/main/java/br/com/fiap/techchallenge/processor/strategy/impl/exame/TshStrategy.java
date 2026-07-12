package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.TshExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class TshStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_TSH";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame de TSH (Hormônio Tireoestimulante) em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"tshBasal\": 0.0,\n" +
                "  \"valoresReferencia\": \"Valores de referência para adultos\",\n" +
                "  \"notaReferenciaGestantes\": \"Valores e notas específicas para gestantes por trimestre, caso listados\",\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Soro)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Quimioluminescência)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame de TSH\"\n" +
                "}\n" +
                "Se algum valor não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        TshExame tsh = new TshExame();
        tsh.setFileName(fileName);
        tsh.setFilePath(storedFilePath);
        tsh.setUploadedAt(uploadTime);
        tsh.setExameTipo("TSH");
        tsh.setMaterial(getNullableText(dataNode, "material"));
        tsh.setMetodo(getNullableText(dataNode, "metodo"));
        tsh.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        tsh.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        tsh.setObservacoes(getList(dataNode, "observacoes"));
        tsh.setNotas(getList(dataNode, "notas"));
        tsh.setTshBasal(getNullableDouble(dataNode, "tshBasal"));
        tsh.setValoresReferencia(getNullableText(dataNode, "valoresReferencia"));
        tsh.setNotaReferenciaGestantes(getNullableText(dataNode, "notaReferenciaGestantes"));
        tsh.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        tsh.persist();
        return tsh;
    }
}
