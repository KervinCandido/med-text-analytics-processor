package br.com.fiap.techchallenge.processor.service.ia.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.HemogramaExameEntity;
import br.com.fiap.techchallenge.processor.service.ia.DocumentExtractDataIAService;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@SystemMessage(
    """
    === REGRAS CRÍTICAS DE PRIVACIDADE E ANOMINIZAÇÃO ===
    Para conformidade com leis de proteção de dados, NÃO INCLUA em nenhuma circunstância nomes de pessoas físicas
    (como paciente, médico, etc.), nomes de clínicas, laboratórios, hospitais, locais de atendimento, números de registro profissional
    (como CRM, etc.), CPFs, RGs ou endereços físicos nos campos de texto livre preenchidos no JSON 
    (especialmente em 'descricaoGeral', 'observacoes', 'notas', etc.).
    Caso precise descrever alguma ação ou local, substitua os nomes específicos por termos totalmente neutros e genéricos 
    (ex: substitua 'Maria Lopes Ramos' por 'o paciente'; substitua 'Pronto Atendimento Dr. José Lins' por 'o pronto atendimento' ou 'a instituição emissora').
    Analise este hemograma em português e extraia as seguintes informações no formato JSON:
    {
      "eritrograma": {
        "hemacias": 0.0,
        "hemoglobina": 0.0,
        "hematocrito": 0.0,
        "vcm": 0.0,
        "hcm": 0.0,
        "chcm": 0.0,
        "rdw": 0.0
      },
      "leucograma": {
        "leucocitosTotais": 0.0,
        "neutrofilosRelativo": 0.0,
        "neutrofilosAbsoluto": 0.0,
        "neutrofilosSegmentadosRelativo": 0.0,
        "neutrofilosSegmentadosAbsoluto": 0.0,
        "neutrofilosBastoesRelativo": 0.0,
        "neutrofilosBastoesAbsoluto": 0.0,
        "linfocitosRelativo": 0.0,
        "linfocitosAbsoluto": 0.0,
        "monocitosRelativo": 0.0,
        "monocitosAbsoluto": 0.0,
        "eosinofilosRelativo": 0.0,
        "eosinofilosAbsoluto": 0.0,
        "basofilosRelativo": 0.0,
        "basofilosAbsoluto": 0.0
      },
      "plaquetograma": {
        "plaquetas": 0.0,
        "vpm": 0.0
      },
      "material": "Amostra de material utilizada (ex: Sangue total, Soro)",
      "metodo": "Metodologia de análise (ex: Citometria de fluxo)",
      "dataColeta": "Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "dataLiberacao": "Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo ou observações descritas no exame"
    }
    Se algum valor numérico ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "hemograma")
public interface HemogramaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    HemogramaExameEntity extractData(Image image);
}
