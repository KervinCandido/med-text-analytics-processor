package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.T4LivreExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class T4LivreStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_T4_LIVRE";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame de T4 Livre (também chamado de Tiroxina Livre) em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"t4Livre\": 0.0,\n" +
                "  \"valoresReferencia\": \"Valores de referência para adultos\",\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Soro)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Quimioluminescência)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame de T4 Livre\"\n" +
                "}\n" +
                "Se algum valor não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        T4LivreExame t4 = new T4LivreExame();
        t4.setFileName(fileName);
        t4.setFilePath(storedFilePath);
        t4.setUploadedAt(uploadTime);
        t4.setExameTipo("T4_LIVRE");
        t4.setMaterial(getNullableText(dataNode, "material"));
        t4.setMetodo(getNullableText(dataNode, "metodo"));
        t4.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        t4.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        t4.setObservacoes(getList(dataNode, "observacoes"));
        t4.setNotas(getList(dataNode, "notas"));
        t4.setT4Livre(getNullableDouble(dataNode, "t4Livre"));
        t4.setValoresReferencia(getNullableText(dataNode, "valoresReferencia"));
        t4.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        t4.persist();
        return t4;
    }
}
