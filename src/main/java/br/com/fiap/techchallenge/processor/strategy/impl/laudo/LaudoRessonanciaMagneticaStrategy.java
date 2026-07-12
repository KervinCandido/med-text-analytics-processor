package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class LaudoRessonanciaMagneticaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_RESSONANCIA_MAGNETICA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de exame de Ressonância Magnética em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"areaCorpo\": \"Área ou região do corpo analisada (ex: Crânio, Ombro, Coluna Lombar, Joelho, Abdome)\",\n" +
                "  \"tecnica\": \"Técnica do exame, incluindo as sequências de pulso utilizadas, planos anatômicos e se houve uso de contraste (ex: Sequências multiplanares ponderadas em T1 e T2, com ou sem contraste paramagnético gadolínio)\",\n" +
                "  \"descricaoAnatomica\": \"Relato detalhado sobre a morfologia, contornos, sinal e aspecto dos tecidos, ossos e estruturas anatômicas da região analisada\",\n" +
                "  \"achadosNormais\": \"Confirmação das estruturas que se encontram normais e sem alterações de sinal ou morfologia\",\n" +
                "  \"achadosPatologicos\": \"Descrição minuciosa de qualquer lesão, herniação, rotura de tendão, edema ósseo, nódulo, tumor ou anomalia encontrada, incluindo as suas medidas e localização exata\",\n" +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica, hipótese ou conclusão final do laudo da ressonância magnética\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo de ressonância magnética\"\n" +
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
        laudo.setLaudoTipo("RESSONANCIA_MAGNETICA");
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
