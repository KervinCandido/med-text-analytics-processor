package br.com.fiap.techchallenge.processor.service.ia.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.MamografiaLaudoEntity;
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
    Analise este laudo de exame de Mamografia em português e extraia as seguintes informações no formato JSON:
    {
      "indicacaoClinica": "Indicação clínica ou motivo do exame. Classifique rigorosamente como 'Rastreamento' (se preventivo/rotina, sem sintomas) ou 'Diagnóstico' (se para investigar dor, nódulos palpáveis ou outros sintomas)",
      "composicaoDensidade": "Composição e densidade cútaneo-glandular das mamas. Classifique estritamente como 'Tipo A' (quase totalmente gordurosas), 'Tipo B' (áreas esparsas de densidade), 'Tipo C' (heterogeneamente densas) ou 'Tipo D' (extremamente densas) com base na descrição contida no laudo",
      "descricaoAchados": "Análise comparativa ou estudo comparativo das mamas em relação a exames anteriores ou comparativamente entre as duas mamas. Extraia o conteúdo localizado geralmente sob os títulos 'Análise Comparativa', 'Estudo Comparativo', 'Estudo Anterior' ou 'Achados' (descrevendo semelhanças, alterações evolutivas, estabilidade ou ausência de exames anteriores para comparação)",
      "tecnica": "Técnica ou Metodologia utilizada no exame (ex: 'Mamografia digital bilateral', 'Incidências crânio-caudal e médio-lateral oblíqua')",
      "recomendacaoClinica": "Orientação ou recomendação clínica de seguimento recomendada pelo radiologista (ex: 'Repetir o rastreamento em 1 ano', 'Realizar controle em 6 meses', 'Prosseguir com investigação histopatológica (biópsia)')",
      "categoriaBirads": "Categoria BI-RADS identificada no laudo (ex: 'BI-RADS 0', 'BI-RADS 1', 'BI-RADS 2', 'BI-RADS 3', 'BI-RADS 4' (ou 4A/4B/4C), 'BI-RADS 5' ou 'BI-RADS 6')",
      "descricaoAnatomica": "Relato anatômico geral contido no laudo das mamas e axilas",
      "achadosNormais": "Confirmação das estruturas que se encontram normais e sem alterações",
      "achadosPatologicos": "Descrição resumida de qualquer achado patológico, suas medidas e localização",
      "impressaoDiagnostica": "Síntese diagnóstica e conclusão final do laudo de mamografia",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo de mamografia"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-mamografia")
public interface LaudoMamografiaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    MamografiaLaudoEntity extractData(Image image);
}
