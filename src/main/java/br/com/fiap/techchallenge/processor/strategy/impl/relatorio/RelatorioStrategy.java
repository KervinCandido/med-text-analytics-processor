package br.com.fiap.techchallenge.processor.strategy.impl.relatorio;

import br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class RelatorioStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "RELATORIO";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este relatório médico em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"historicoClinico\": \"Histórico clínico e evolução da doença. Descreva sintomas iniciais, tempo de evolução da condição e tratamentos prévios realizados (cirurgias, internações, medicamentos)\",\n" +
                "  \"diagnostico\": \"Diagnóstico atual. Descrição clara da patologia ou lesão identificada\",\n" +
                "  \"cid\": \"Código CID (Classificação Internacional de Doenças), se estiver explícito no documento, senão null\",\n" +
                "  \"estadoAtual\": \"Estado atual do paciente. Descreva limitações funcionais, sintomas persistentes, estabilidade ou limitações físicas/cognitivas relativas ao exame físico e exames complementares\",\n" +
                "  \"condutaMedica\": \"Conduta médica atual. Descreva medicamentos prescritos com dosagens e terapias em andamento\",\n" +
                "  \"prognostico\": \"Estimativa de evolução ou recuperação da doença (ex: crônica, degenerativa, reversível)\",\n" +
                "  \"finalidade\": \"Finalidade declarada do relatório se houver (ex: 'Para fins de perícia previdenciária', 'Para fins de viagem aérea', 'Para adaptação escolar'), senão null\",\n" +
                "  \"dataRelatorio\": \"Data de emissão do relatório médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do relatório médico\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Relatorio relatorio = new Relatorio();
        relatorio.setFileName(fileName);
        relatorio.setFilePath(storedFilePath);
        relatorio.setUploadedAt(uploadTime);
        relatorio.setHistoricoClinico(getNullableText(dataNode, "historicoClinico"));
        relatorio.setDiagnostico(getNullableText(dataNode, "diagnostico"));
        relatorio.setCid(getNullableText(dataNode, "cid"));
        relatorio.setEstadoAtual(getNullableText(dataNode, "estadoAtual"));
        relatorio.setCondutaMedica(getNullableText(dataNode, "condutaMedica"));
        relatorio.setPrognostico(getNullableText(dataNode, "prognostico"));
        relatorio.setFinalidade(getNullableText(dataNode, "finalidade"));
        relatorio.setDataRelatorio(getNullableDateTime(dataNode, "dataRelatorio"));
        relatorio.setObservacoes(getList(dataNode, "observacoes"));
        relatorio.setNotas(getList(dataNode, "notas"));
        relatorio.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        relatorio.persist();
        return relatorio;
    }
}
