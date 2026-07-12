package br.com.fiap.techchallenge.processor.strategy.impl.encaminhamento;

import br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class EncaminhamentoStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "ENCAMINHAMENTO";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise esta guia ou encaminhamento médico em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"especialidadeDestino\": \"Especialidade médica de destino/especialista solicitado (ex: Endocrinologista, Cardiologista)\",\n" +
                "  \"instituicaoDestino\": \"Serviço, hospital, UBS ou ambulatório de destino, se houver\",\n" +
                "  \"motivo\": \"Hipótese diagnóstica ou diagnóstico confirmado (incluindo código CID, se houver) que justifica o encaminhamento\",\n" +
                "  \"resumoCaso\": \"Resumo do caso: sintomas, exames relevantes já feitos, tratamentos já tentados e medicamentos em uso\",\n" +
                "  \"objetivo\": \"Objetivo do encaminhamento (ex: 'Avaliação diagnóstica', 'Definição de conduta cirúrgica', 'Acompanhamento compartilhado')\",\n" +
                "  \"prioridade\": \"Nível de prioridade ou classificação de risco (Emergência, Urgência, Prioritário, Eletivo/Rotina) se houver, senão null\",\n" +
                "  \"dataEmissao\": \"Data de emissão do encaminhamento médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo geral descritivo do encaminhamento médico\"\n" +
                "}\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Encaminhamento encaminhamento = new Encaminhamento();
        encaminhamento.setFileName(fileName);
        encaminhamento.setFilePath(storedFilePath);
        encaminhamento.setUploadedAt(uploadTime);
        encaminhamento.setEspecialidadeDestino(getNullableText(dataNode, "especialidadeDestino"));
        encaminhamento.setInstituicaoDestino(getNullableText(dataNode, "instituicaoDestino"));
        encaminhamento.setMotivo(getNullableText(dataNode, "motivo"));
        encaminhamento.setResumoCaso(getNullableText(dataNode, "resumoCaso"));
        encaminhamento.setObjetivo(getNullableText(dataNode, "objetivo"));
        encaminhamento.setPrioridade(getNullableText(dataNode, "prioridade"));
        encaminhamento.setDataEmissao(getNullableDateTime(dataNode, "dataEmissao"));
        encaminhamento.setObservacoes(getList(dataNode, "observacoes"));
        encaminhamento.setNotas(getList(dataNode, "notas"));
        encaminhamento.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        encaminhamento.persist();
        return encaminhamento;
    }
}
