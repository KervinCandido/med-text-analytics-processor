package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LaudoUsObstetricoEndovaginalStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_US_OBSTETRICO_ENDOVAGINAL";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de ultrassom obstétrico endovaginal em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"descricaoAnatomica\": \"Relato detalhado sobre o útero, ovários, saco gestacional, vesícula vitelina, embrião, colo uterino e outras estruturas analisadas\",\n" +
                "  \"achadosNormais\": \"Confirmação das estruturas gestacionais ou anexiais que se encontram normais e sem alterações\",\n" +
                "  \"achadosPatologicos\": \"Descrição minuciosa de descolamentos, hematomas, cistos ovarianos, anomalias no saco gestacional ou embrião, incluindo suas medidas e localização exata\",\n" +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica, idade gestacional estimada (semanas/dias), batimentos cardíacos fetais (BCF) e conclusão final do laudo obstétrico endovaginal\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo obstétrico endovaginal\"\n" +
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
        laudo.setLaudoTipo("US_OBSTETRICO_ENDOVAGINAL");
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
