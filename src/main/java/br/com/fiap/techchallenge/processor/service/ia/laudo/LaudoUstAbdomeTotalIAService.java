package br.com.fiap.techchallenge.processor.service.ia.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.LaudoEntity;
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
    Analise este laudo de ultrassonografia do abdome total em português e extraia as seguintes informações no formato JSON:
    {
      "descricaoAnatomica": "Relato detalhado sobre o tamanho, formato, textura e aspecto dos órgãos abdominais analisados (ex: fígado, vesícula biliar, pâncreas, baço, rins, bexiga, aorta abdominal)",
      "achadosNormais": "Confirmação das estruturas e órgãos abdominais que não apresentam alterações",
      "achadosPatologicos": "Descrição minuciosa de qualquer lesão, nódulo, cisto, cálculo (pedra), esteatose (gordura), inflamação ou anomalia encontrada no abdome, incluindo medidas e localização exata",
      "impressaoDiagnostica": "Síntese diagnóstica, hipótese ou conclusão final do laudo da ultrassonografia",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo abdominal"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-ust-abdome-total")
public interface LaudoUstAbdomeTotalIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    LaudoEntity extractData(Image image);
}
