package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.fiap.techchallenge.processor.domain.laudo.MamografiaLaudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LaudoMamografiaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_MAMOGRAFIA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de exame de Mamografia em português e extraia as seguintes informações no formato JSON:\n"
                +
                "{\n" +
                "  \"indicacaoClinica\": \"Indicação clínica ou motivo do exame. Classifique rigorosamente como 'Rastreamento' (se preventivo/rotina, sem sintomas) ou 'Diagnóstico' (se para investigar dor, nódulos palpáveis ou outros sintomas)\",\n"
                +
                "  \"composicaoDensidade\": \"Composição e densidade cútaneo-glandular das mamas. Classifique estritamente como 'Tipo A' (quase totalmente gordurosas), 'Tipo B' (áreas esparsas de densidade), 'Tipo C' (heterogeneamente densas) ou 'Tipo D' (extremamente densas) com base na descrição contida no laudo\",\n"
                +
                "  \"descricaoAchados\": \"Análise comparativa ou estudo comparativo das mamas em relação a exames anteriores ou comparativamente entre as duas mamas. Extraia o conteúdo localizado geralmente sob os títulos 'Análise Comparativa', 'Estudo Comparativo', 'Estudo Anterior' ou 'Achados' (descrevendo semelhanças, alterações evolutivas, estabilidade ou ausência de exames anteriores para comparação)\",\n"
                +
                "  \"tecnica\": \"Técnica ou Metodologia utilizada no exame (ex: 'Mamografia digital bilateral', 'Incidências crânio-caudal e médio-lateral oblíqua')\",\n"
                +
                "  \"recomendacaoClinica\": \"Orientação ou recomendação clínica de seguimento recomendada pelo radiologista (ex: 'Repetir o rastreamento em 1 ano', 'Realizar controle em 6 meses', 'Prosseguir com investigação histopatológica (biópsia)')\",\n"
                +
                "  \"categoriaBirads\": \"Categoria BI-RADS identificada no laudo (ex: 'BI-RADS 0', 'BI-RADS 1', 'BI-RADS 2', 'BI-RADS 3', 'BI-RADS 4' (ou 4A/4B/4C), 'BI-RADS 5' ou 'BI-RADS 6')\",\n"
                +
                "  \"descricaoAnatomica\": \"Relato anatômico geral contido no laudo das mamas e axilas\",\n" +
                "  \"achadosNormais\": \"Confirmação das estruturas que se encontram normais e sem alterações\",\n" +
                "  \"achadosPatologicos\": \"Descrição resumida de qualquer achado patológico, suas medidas e localização\",\n"
                +
                "  \"impressaoDiagnostica\": \"Síntese diagnóstica e conclusão final do laudo de mamografia\",\n" +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo de mamografia\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath,
            LocalDateTime uploadTime) {
        MamografiaLaudo laudo = new MamografiaLaudo();
        laudo.setFileName(fileName);
        laudo.setFilePath(storedFilePath);
        laudo.setUploadedAt(uploadTime);
        laudo.setLaudoTipo("MAMOGRAFIA");
        laudo.setAreaCorpo("Mamas");
        laudo.setIndicacaoClinica(getNullableText(dataNode, "indicacaoClinica"));
        laudo.setComposicaoDensidade(getNullableText(dataNode, "composicaoDensidade"));
        laudo.setDescricaoAchados(getNullableText(dataNode, "descricaoAchados"));
        laudo.setTecnica(getNullableText(dataNode, "tecnica"));
        laudo.setRecomendacaoClinica(getNullableText(dataNode, "recomendacaoClinica"));
        laudo.setCategoriaBirads(getNullableText(dataNode, "categoriaBirads"));
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
