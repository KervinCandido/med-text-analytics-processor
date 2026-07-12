package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.BetaHcgExame;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class BetaHcgStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_BETA_HCG";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este exame de Beta-HCG em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"betaHcgQuantitativo\": 0.0,\n" +
                "  \"resultadoQualitativo\": \"Indicação (Positivo ou Negativo, ou Reagente/Não Reagente, se houver)\",\n" +
                "  \"valoresReferencia\": \"Valores de referência normais (ex: Negativo para gestação < 5 mUI/mL, Inconclusivo 5 a 25 mUI/mL)\",\n" +
                "  \"idadeGestacionalTabela\": \"Tabela estimada de relação entre valores e semanas de gravidez listada no exame\",\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Soro)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Eletroquimioluminescência)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame de Beta-HCG\"\n" +
                "}\n" +
                "Se algum valor numérico ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        BetaHcgExame hcg = new BetaHcgExame();
        hcg.setFileName(fileName);
        hcg.setFilePath(storedFilePath);
        hcg.setUploadedAt(uploadTime);
        hcg.setExameTipo("BETA_HCG");
        hcg.setMaterial(getNullableText(dataNode, "material"));
        hcg.setMetodo(getNullableText(dataNode, "metodo"));
        hcg.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        hcg.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        hcg.setObservacoes(getList(dataNode, "observacoes"));
        hcg.setNotas(getList(dataNode, "notas"));
        hcg.setBetaHcgQuantitativo(getNullableDouble(dataNode, "betaHcgQuantitativo"));
        hcg.setResultadoQualitativo(getNullableText(dataNode, "resultadoQualitativo"));
        hcg.setValoresReferencia(getNullableText(dataNode, "valoresReferencia"));
        hcg.setIdadeGestacionalTabela(getNullableText(dataNode, "idadeGestacionalTabela"));
        hcg.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        hcg.persist();
        return hcg;
    }
}
