package br.com.fiap.techchallenge.processor.service.ia.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.BetaHcgExameEntity;
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
    Analise este exame de Beta-HCG em português e extraia as seguintes informações no formato JSON:
    {
      "betaHcgQuantitativo": 0.0,
      "resultadoQualitativo": "Indicação (Positivo ou Negativo, ou Reagente/Não Reagente, se houver)",
      "valoresReferencia": "Valores de referência normais (ex: Negativo para gestação < 5 mUI/mL, Inconclusivo 5 a 25 mUI/mL)",
      "idadeGestacionalTabela": "Tabela estimada de relação entre valores e semanas de gravidez listada no exame",
      "material": "Amostra de material utilizada (ex: Soro)",
      "metodo": "Metodologia de análise (ex: Eletroquimioluminescência)",
      "dataColeta": "Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "dataLiberacao": "Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo ou observações descritas no exame de Beta-HCG"
    }
    Se algum valor numérico ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "beta-hcg")
public interface BetaHcgIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    BetaHcgExameEntity extractData(Image image);
}
