package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.fiap.techchallenge.processor.domain.laudo.EndoscopiaLaudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LaudoEndoscopiaDigestivaAltaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_ENDOSCOPIA_DIGESTIVA_ALTA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de exame de Endoscopia Digestiva Alta (EDA) em português e extraia as seguintes informações no formato JSON:\n"
                +
                "{\n" +
                "  \"tecnica\": \"Dados técnicos e medicamentosos do exame. Inclua o modelo do aparelho (videoendoscópio), sedação/anestesia utilizada (ex: Propofol, Midazolam, Lidocaína spray) e qualidade do exame/tolerância do paciente\",\n"
                +
                "  \"analiseEsofago\": \"Análise detalhada do esôfago. Inclua informações de calibre/luz, mucosa (ex: esofagite, úlceras, varizes), transição esôfago-gástrica/linha Z e pinçamento diafragmático (presença/ausência de hérnia de hiato)\",\n"
                +
                "  \"analiseEstomago\": \"Análise detalhada do estômago. Inclua forma/contratilidade, luz/conteúdo (normal, presença de bile/sangue), mucosa por regiões (fundo, corpo e antro) e manobra de retroflexão/retrovisão\",\n"
                +
                "  \"analiseDuodeno\": \"Análise detalhada do bulbo duodenal e segunda porção (mucosa e vilosidades)\",\n"
                +
                "  \"procedimentosAdicionais\": \"Qualquer procedimento adicional realizado, como biópsias (região coletada), teste de urease para H. pylori (positivo/negativo) ou intervenções terapêuticas (retirada de pólipos, cauterização)\",\n"
                +
                "  \"impressaoDiagnostica\": \"Conclusão ou impressão diagnóstica final resumindo os achados (ex: gastrite enantematosa, esofagite, hérnia de hiato)\",\n"
                +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo de endoscopia digestiva alta\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath,
            LocalDateTime uploadTime) {
        EndoscopiaLaudo laudo = new EndoscopiaLaudo();
        laudo.setFileName(fileName);
        laudo.setFilePath(storedFilePath);
        laudo.setUploadedAt(uploadTime);
        laudo.setLaudoTipo("ENDOSCOPIA_DIGESTIVA_ALTA");
        laudo.setAreaCorpo("Aparelho Digestivo");
        laudo.setTecnica(getNullableText(dataNode, "tecnica"));
        laudo.setAnaliseEsofago(getNullableText(dataNode, "analiseEsofago"));
        laudo.setAnaliseEstomago(getNullableText(dataNode, "analiseEstomago"));
        laudo.setAnaliseDuodeno(getNullableText(dataNode, "analiseDuodeno"));
        laudo.setProcedimentosAdicionais(getNullableText(dataNode, "procedimentosAdicionais"));
        laudo.setImpressaoDiagnostica(getNullableText(dataNode, "impressaoDiagnostica"));
        laudo.setDataLaudo(getNullableDateTime(dataNode, "dataLaudo"));
        laudo.setObservacoes(getList(dataNode, "observacoes"));
        laudo.setNotas(getList(dataNode, "notas"));
        laudo.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        laudo.persist();
        return laudo;
    }
}
