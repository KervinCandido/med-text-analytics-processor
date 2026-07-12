package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LaudoTomografiaComputadorizadaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_TOMOGRAFIA_COMPUTADORIZADA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de exame de Tomografia Computadorizada (TC) em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"areaCorpo\": \"Região ou área do corpo analisada (ex: Tórax, Abdome Total, Crânio, Seios da Face)\",\n" +
                "  \"tecnica\": \"Técnica do exame, indicando a metodologia de aquisição de imagens e o uso de contraste iodado (ex: Exame realizado com cortes finos multiplanares helicoidais, sem e com administração de contraste iodado endovenoso)\",\n" +
                "  \"descricaoAnatomica\": \"Relato detalhado da morfologia, atenuação/densidade, contornos e aspecto anatômico das estruturas e órgãos analisados\",\n" +
                "  \"achadosNormais\": \"Confirmação das estruturas corporais que se apresentam normais e sem alterações estruturais ou de atenuação\",\n" +
                "  \"achadosPatologicos\": \"Descrição minuciosa de qualquer lesão, nódulo, consolidação pulmonar, derrame pleural, cisto, cálculo ou anomalia encontrada, incluindo as suas medidas e localização exata\",\n" +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica, hipótese, conclusões ou classificações padronizadas listadas no laudo\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo de tomografia computadorizada\"\n" +
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
        laudo.setLaudoTipo("TOMOGRAFIA_COMPUTADORIZADA");
        laudo.setAreaCorpo(getNullableText(dataNode, "areaCorpo"));
        laudo.setTecnica(getNullableText(dataNode, "tecnica"));
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
