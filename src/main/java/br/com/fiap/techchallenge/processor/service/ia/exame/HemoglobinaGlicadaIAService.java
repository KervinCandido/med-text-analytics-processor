package br.com.fiap.techchallenge.processor.service.ia.exame;

import br.com.fiap.techchallenge.processor.domain.exame.HemoglobinaGlicadaExame;
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
    Analise este exame de hemoglobina glicada (HbA1c) em português e extraia as seguintes informações no formato JSON:
    {
      "hemoglobinaGlicada": 0.0,
      "valoresReferencia": "Texto descrevendo os parâmetros de referência (ex: Normal: abaixo de 5,7%...)",
      "glicemiaMediaEstimada": 0.0,
      "material": "Amostra de material utilizada (ex: Sangue total EDTA)",
      "metodo": "Metodologia de análise (ex: HPLC, Imunoensaio)",
      "dataColeta": "Data/hora da coleta em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "dataLiberacao": "Data/hora da liberação em formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo ou observações descritas no exame de hemoglobina glicada"
    }
    Se algum valor não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "hemoglobina-glicada")
public interface HemoglobinaGlicadaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    HemoglobinaGlicadaExame extractData(Image image);
}
