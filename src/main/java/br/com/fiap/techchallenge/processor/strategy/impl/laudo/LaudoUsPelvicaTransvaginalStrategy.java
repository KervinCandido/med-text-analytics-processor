package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LaudoUsPelvicaTransvaginalStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_US_PELVICA_TRANSVAGINAL";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de ultrassom pélvico transvaginal em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"descricaoAnatomica\": \"Relato detalhado sobre útero (posição, volume, miométrio), endométrio (espessura, aspecto), e ovários (volume, folículos)\",\n" +
                "  \"achadosNormais\": \"Confirmação das estruturas pélvicas que não apresentam alterações\",\n" +
                "  \"achadosPatologicos\": \"Descrição minuciosa de pólipos, miomas, cistos complexos, líquido livre na pelve ou qualquer anomalia encontrada, incluindo medidas e localização exata\",\n" +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica, hipótese e conclusão final do laudo pélvico transvaginal\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo pélvico transvaginal\"\n" +
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
        laudo.setLaudoTipo("US_PELVICA_TRANSVAGINAL");
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
