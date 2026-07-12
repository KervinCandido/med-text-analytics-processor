package br.com.fiap.techchallenge.processor.strategy.impl.exame;

import br.com.fiap.techchallenge.processor.domain.exame.Eritrograma;
import br.com.fiap.techchallenge.processor.domain.exame.HemogramaExame;
import br.com.fiap.techchallenge.processor.domain.exame.Leucograma;
import br.com.fiap.techchallenge.processor.domain.exame.Plaquetograma;
import br.com.fiap.techchallenge.processor.strategy.BaseStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class HemogramaStrategy extends BaseStrategy {

    @Override
    public String getClassification() {
        return "EXAME_HEMOGRAMA";
    }

    @Override
    public String getPromptInstruction() {
        return "Analise este hemograma em português e extraia as seguintes informações no formato JSON:\n" +
                "{\n" +
                "  \"eritrograma\": {\n" +
                "    \"hemacias\": 0.0,\n" +
                "    \"hemoglobina\": 0.0,\n" +
                "    \"hematocrito\": 0.0,\n" +
                "    \"vcm\": 0.0,\n" +
                "    \"hcm\": 0.0,\n" +
                "    \"chcm\": 0.0,\n" +
                "    \"rdw\": 0.0\n" +
                "  },\n" +
                "  \"leucograma\": {\n" +
                "    \"leucocitosTotais\": 0.0,\n" +
                "    \"neutrofilosRelativo\": 0.0,\n" +
                "    \"neutrofilosAbsoluto\": 0.0,\n" +
                "    \"neutrofilosSegmentadosRelativo\": 0.0,\n" +
                "    \"neutrofilosSegmentadosAbsoluto\": 0.0,\n" +
                "    \"neutrofilosBastoesRelativo\": 0.0,\n" +
                "    \"neutrofilosBastoesAbsoluto\": 0.0,\n" +
                "    \"linfocitosRelativo\": 0.0,\n" +
                "    \"linfocitosAbsoluto\": 0.0,\n" +
                "    \"monocitosRelativo\": 0.0,\n" +
                "    \"monocitosAbsoluto\": 0.0,\n" +
                "    \"eosinofilosRelativo\": 0.0,\n" +
                "    \"eosinofilosAbsoluto\": 0.0,\n" +
                "    \"basofilosRelativo\": 0.0,\n" +
                "    \"basofilosAbsoluto\": 0.0\n" +
                "  },\n" +
                "  \"plaquetograma\": {\n" +
                "    \"plaquetas\": 0.0,\n" +
                "    \"vpm\": 0.0\n" +
                "  },\n" +
                "  \"material\": \"Amostra de material utilizada (ex: Sangue total, Soro)\",\n" +
                "  \"metodo\": \"Metodologia de análise (ex: Citometria de fluxo)\",\n" +
                "  \"dataColeta\": \"Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"dataLiberacao\": \"Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)\",\n" +
                "  \"observacoes\": [],\n" +
                "  \"notas\": [],\n" +
                "  \"descricaoGeral\": \"Resumo ou observações descritas no exame\"\n" +
                "}\n" +
                "Se algum valor numérico ou data não for encontrado ou não estiver legível, retorne null para ele.\n" +
                "Retorne apenas o JSON, sem markdown ou texto adicional.";
    }

    @Override
    public PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime) {
        HemogramaExame hemograma = new HemogramaExame();
        hemograma.setFileName(fileName);
        hemograma.setFilePath(storedFilePath);
        hemograma.setUploadedAt(uploadTime);
        hemograma.setExameTipo("HEMOGRAMA");
        hemograma.setMaterial(getNullableText(dataNode, "material"));
        hemograma.setMetodo(getNullableText(dataNode, "metodo"));
        hemograma.setDataColeta(getNullableDateTime(dataNode, "dataColeta"));
        hemograma.setDataLiberacao(getNullableDateTime(dataNode, "dataLiberacao"));
        hemograma.setObservacoes(getList(dataNode, "observacoes"));
        hemograma.setNotas(getList(dataNode, "notas"));

        // Eritrograma
        if (dataNode.has("eritrograma")) {
            JsonNode erNode = dataNode.get("eritrograma");
            Eritrograma er = new Eritrograma();
            er.setHemacias(getNullableDouble(erNode, "hemacias"));
            er.setHemoglobina(getNullableDouble(erNode, "hemoglobina"));
            er.setHematocrito(getNullableDouble(erNode, "hematocrito"));
            er.setVcm(getNullableDouble(erNode, "vcm"));
            er.setHcm(getNullableDouble(erNode, "hcm"));
            er.setChcm(getNullableDouble(erNode, "chcm"));
            er.setRdw(getNullableDouble(erNode, "rdw"));
            hemograma.setEritrograma(er);
        }

        // Leucograma
        if (dataNode.has("leucograma")) {
            JsonNode leuNode = dataNode.get("leucograma");
            Leucograma leu = new Leucograma();
            leu.setLeucocitosTotais(getNullableDouble(leuNode, "leucocitosTotais"));
            leu.setNeutrofilosRelativo(getNullableDouble(leuNode, "neutrofilosRelativo"));
            leu.setNeutrofilosAbsoluto(getNullableDouble(leuNode, "neutrofilosAbsoluto"));
            leu.setNeutrofilosSegmentadosRelativo(getNullableDouble(leuNode, "neutrofilosSegmentadosRelativo"));
            leu.setNeutrofilosSegmentadosAbsoluto(getNullableDouble(leuNode, "neutrofilosSegmentadosAbsoluto"));
            leu.setNeutrofilosBastoesRelativo(getNullableDouble(leuNode, "neutrofilosBastoesRelativo"));
            leu.setNeutrofilosBastoesAbsoluto(getNullableDouble(leuNode, "neutrofilosBastoesAbsoluto"));
            leu.setLinfocitosRelativo(getNullableDouble(leuNode, "linfocitosRelativo"));
            leu.setLinfocitosAbsoluto(getNullableDouble(leuNode, "linfocitosAbsoluto"));
            leu.setMonocitosRelativo(getNullableDouble(leuNode, "monocitosRelativo"));
            leu.setMonocitosAbsoluto(getNullableDouble(leuNode, "monocitosAbsoluto"));
            leu.setEosinofilosRelativo(getNullableDouble(leuNode, "eosinofilosRelativo"));
            leu.setEosinofilosAbsoluto(getNullableDouble(leuNode, "eosinofilosAbsoluto"));
            leu.setBasofilosRelativo(getNullableDouble(leuNode, "basofilosRelativo"));
            leu.setBasofilosAbsoluto(getNullableDouble(leuNode, "basofilosAbsoluto"));
            hemograma.setLeucograma(leu);
        }

        // Plaquetograma
        if (dataNode.has("plaquetograma")) {
            JsonNode plaqNode = dataNode.get("plaquetograma");
            Plaquetograma plaq = new Plaquetograma();
            plaq.setPlaquetas(getNullableDouble(plaqNode, "plaquetas"));
            plaq.setVpm(getNullableDouble(plaqNode, "vpm"));
            hemograma.setPlaquetograma(plaq);
        }

        hemograma.setDescricaoGeral(getNullableText(dataNode, "descricaoGeral"));
        hemograma.persist();
        return hemograma;
    }
}
