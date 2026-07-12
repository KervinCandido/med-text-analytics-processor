package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LaudoUsObstetricoDopplerStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_US_OBSTETRICO_DOPPLER";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de ultrassom obstétrico com doppler em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"descricaoAnatomica\": \"Relato detalhado da anatomia fetal, placenta, cordão umbilical, líquido amniótico e índices de fluxo Doppler (artéria umbilical, artéria cerebral média fetal, artérias uterinas maternas)\",\n" +
                "  \"achadosNormais\": \"Confirmação de parâmetros de crescimento, morfologia e índices de fluxo circulatório (doppler) normais\",\n" +
                "  \"achadosPatologicos\": \"Descrição minuciosa de alterações morfofuncionais fetais, centralização fetal, alterações de fluxo placentário ou cordão, incluindo medidas e localização exata\",\n" +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica, estimativa de peso fetal, idade gestacional e conclusão final sobre a vitalidade fetal baseada no doppler\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo obstétrico com doppler\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Laudo laudo = new Laudo();
        laudo.setFileName(fileName);
        laudo.setFilePath(storedFilePath);
        laudo.setUploadedAt(uploadTime);
        laudo.setLaudoTipo("US_OBSTETRICO_DOPPLER");
        laudo.setDescricaoAnatomica(getNullableText(dataNode, "descricaoAnatomica"));
        laudo.setAchadosNormais(getNullableText(dataNode, "achadosNormais"));
        laudo.setAchadosPatologicos(getNullableText(dataNode, "achadosPatologicos"));
        laudo.setImpressaoDiagnostica(getNullableText(dataNode, "impressaoDiagnostica"));
        laudo.setDataLaudo(getNullableDateTime(dataNode, "dataLaudo"));
        laudo.setObservacoes(getList(dataNode, "observacoes"));
        laudo.setNotas(getList(dataNode, "notas"));
        laudo.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        laudo.persist();
        return laudo;
    }
}
