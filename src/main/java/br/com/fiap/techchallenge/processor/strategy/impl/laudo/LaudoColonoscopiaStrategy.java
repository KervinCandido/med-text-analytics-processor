package br.com.fiap.techchallenge.processor.strategy.impl.laudo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.fiap.techchallenge.processor.domain.laudo.ColonoscopiaLaudo;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LaudoColonoscopiaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "LAUDO_COLONOSCOPIA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este laudo de exame de Colonoscopia em português e extraia as seguintes informações no formato JSON:\n"
                +
                "{\n" +
                "  \"tecnica\": \"Dados técnicos, preparo e sedação do exame. Inclua drogas injetáveis utilizadas para sedação/analgesia (ex: Propofol, Fentanil, Midazolam), qualidade do preparo intestinal (ex: classificação excelente/adequada, Escala de Boston) e a extensão do exame (se ceco foi atingido ou se foi interrompido)\",\n"
                +
                "  \"analiseSegmentar\": \"Descrição detalhada da mucosa, calibre e padrão vascular de cada região/segmento do intestino percorrido (íleo terminal, ceco/cólon ascendente, cólon transverso, cólon descendente/sigmoide, reto e canal anal)\",\n"
                +
                "  \"descricaoLesoes\": \"Descrição detalhada de qualquer lesão/irregularidade (pólipos ou tumores) encontrada. Inclua localização segmentar, tamanho/quantidade e aspecto morfológico (se plana, elevada, pediculada, incluindo Classificação de Paris)\",\n"
                +
                "  \"procedimentosAdicionais\": \"Qualquer intervenção realizada no exame, como biópsias, polipectomia, mucosectomia e a respectiva identificação de frascos de envio (ex: Frasco 1: Pólipo de cólon sigmoide)\",\n"
                +
                "  \"impressaoDiagnostica\": \"Conclusão ou impressão diagnóstica final do exame (ex: doença diverticular, polipectomia, pancolite, exame dentro dos padrões de normalidade)\",\n"
                +
                "  \"dataLaudo\": \"Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do laudo de colonoscopia\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath,
            LocalDateTime uploadTime) {
        ColonoscopiaLaudo laudo = new ColonoscopiaLaudo();
        laudo.setFileName(fileName);
        laudo.setFilePath(storedFilePath);
        laudo.setUploadedAt(uploadTime);
        laudo.setLaudoTipo("COLONOSCOPIA");
        laudo.setAreaCorpo("Aparelho Digestivo");
        laudo.setTecnica(getNullableText(dataNode, "tecnica"));
        laudo.setAnaliseSegmentar(getNullableText(dataNode, "analiseSegmentar"));
        laudo.setDescricaoLesoes(getNullableText(dataNode, "descricaoLesoes"));
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
