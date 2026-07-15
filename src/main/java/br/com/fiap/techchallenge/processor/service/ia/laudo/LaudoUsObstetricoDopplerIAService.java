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
    Analise este laudo de ultrassom obstétrico com doppler em português e extraia as seguintes informações no formato JSON:
    {
      "descricaoAnatomica": "Relato detalhado da anatomia fetal, placenta, cordão umbilical, líquido amniótico e índices de fluxo Doppler (artéria umbilical, artéria cerebral média fetal, artérias uterinas maternas)",
      "achadosNormais": "Confirmação de parâmetros de crescimento, morfologia e índices de fluxo circulatório (doppler) normais",
      "achadosPatologicos": "Descrição minuciosa de alterações morfofuncionais fetais, centralização fetal, alterações de fluxo placentário ou cordão, incluindo medidas e localização exata",
      "impressaoDiagnostica": "Síntese diagnóstica, estimativa de peso fetal, idade gestacional e conclusão final sobre a vitalidade fetal baseada no doppler",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo obstétrico com doppler"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-us-obstetrico-doppler")
public interface LaudoUsObstetricoDopplerIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    LaudoEntity extractData(Image image);
}
