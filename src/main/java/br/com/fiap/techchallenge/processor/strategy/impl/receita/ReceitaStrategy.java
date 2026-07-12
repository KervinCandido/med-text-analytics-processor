package br.com.fiap.techchallenge.processor.strategy.impl.receita;

import br.com.fiap.techchallenge.processor.domain.receita.Receita;
import br.com.fiap.techchallenge.processor.domain.receita.ReceitaItem;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ReceitaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "RECEITA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise esta receita médica em português e extraia as informações estruturadas no seguinte formato JSON:\n" +
                "{\n" +
                "  \"dataReceita\": \"Data da receita (escrita ou impressa) no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss). Se houver apenas data, use HH:mm:ss padrão 00:00:00.\",\n" +
                "  \"itens\": [\n" +
                "    {\n" +
                "      \"nomeMedicamento\": \"Nome comercial ou princípio ativo (genérico)\",\n" +
                "      \"formaFarmaceutica\": \"Forma de apresentação (ex: Comprimido, Gotas, Xarope, Pomada, Injetável)\",\n" +
                "      \"concentracao\": \"Dosagem/Concentração do medicamento (ex: 500mg, 10mg/ml)\",\n" +
                "      \"quantidadeTotal\": \"Quantidade total a ser dispensada (ex: 2 caixas, 1 frasco) OU expressões indicando tratamento contínuo/indeterminado (ex: 'Uso Contínuo', 'Período Indeterminado') quando a quantidade física exata não for declarada.\",\n" +
                "      \"posologiaOrientacoes\": \"Instruções de uso/orientações do paciente (ex: Tomar 1 comprimido de 8 em 8 horas por 7 dias)\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"observacoes\": [\"Observação 1\"], // Lista de observações clínicas, senão houver retorne array vazio\n" +
                "  \"notas\": [\"Nota de rodapé 1\"], // Lista de notas de rodapé ou observações técnicas, senão houver retorne array vazio\n" +
                "  \"descricaoGeral\": \"Resumo geral da receita\"\n" +
                "}\n" +
                "Atenção importante: Caligrafias médicas podem ser de difícil leitura. Faça o melhor esforço possível utilizando o contexto clínico dos medicamentos, dosagens padrão e associações comuns de posologia para decifrar e transcrever corretamente as palavras.\n" +
                "Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        Receita receita = new Receita();
        receita.setFileName(fileName);
        receita.setFilePath(storedFilePath);
        receita.setUploadedAt(uploadTime);
        receita.setDataReceita(getNullableDateTime(dataNode, "dataReceita"));
        receita.setObservacoes(getList(dataNode, "observacoes"));
        receita.setNotas(getList(dataNode, "notas"));

        List<ReceitaItem> itemsList = new ArrayList<>();
        if (dataNode.has("itens") && dataNode.get("itens").isArray()) {
            for (JsonNode itemNode : dataNode.get("itens")) {
                ReceitaItem item = new ReceitaItem();
                item.setNomeMedicamento(getNullableText(itemNode, "nomeMedicamento"));
                item.setFormaFarmaceutica(getNullableText(itemNode, "formaFarmaceutica"));
                item.setConcentracao(getNullableText(itemNode, "concentracao"));
                item.setQuantidadeTotal(getNullableText(itemNode, "quantidadeTotal"));
                item.setPosologiaOrientacoes(getNullableText(itemNode, "posologiaOrientacoes"));
                itemsList.add(item);
            }
        }
        receita.setItens(itemsList);
        receita.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        receita.persist();
        return receita;
    }
}
