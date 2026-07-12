package br.com.fiap.techchallenge.processor.strategy.impl.registro_atendimento;

import br.com.fiap.techchallenge.processor.domain.registro_atendimento.RegistroAtendimento;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class RegistroAtendimentoStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "REGISTRO_ATENDIMENTO";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este registro de atendimento em português e extraia as informações no seguinte formato JSON:\n" +
                "{\n" +
                "  \"paciente\": \"Nome do paciente\",\n" +
                "  \"medico\": \"Nome do médico\",\n" +
                "  \"dataAtendimento\": \"Data do atendimento\",\n" +
                "  \"motivoAtendimento\": \"Motivo do atendimento\",\n" +
                "  \"descricaoGeral\": \"Resumo do atendimento (procedimentos, exames solicitados, etc)\"\n" +
                "}\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        RegistroAtendimento registro = new RegistroAtendimento();
        registro.setFileName(fileName);
        registro.setFilePath(storedFilePath);
        registro.setUploadedAt(uploadTime);
        registro.setPaciente(getNullableText(dataNode, "paciente"));
        registro.setMedico(getNullableText(dataNode, "medico"));
        registro.setDataAtendimento(getNullableText(dataNode, "dataAtendimento"));
        registro.setMotivoAtendimento(getNullableText(dataNode, "motivoAtendimento"));
        registro.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        registro.persist();
        return registro;
    }
}
