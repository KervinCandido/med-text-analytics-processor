package br.com.fiap.techchallenge.processor.service.ia.encaminhamento;

import br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento;
import br.com.fiap.techchallenge.processor.service.ia.DocumentExtractDataIAService;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;

@SystemMessage(
    """
    === REGRAS CRÍTICAS DE PRIVACIDADE E ANOMINIZAÇÃO ===
    Para conformidade com leis de proteção de dados, NÃO INCLUA em nenhuma circunstância nomes de pessoas físicas
    (como paciente, médico, etc.), nomes de clínicas, laboratórios, hospitais, locais de atendimento, números de registro profissional
    (como CRM, etc.), CPFs, RGs ou endereços físicos nos campos de texto livre preenchidos no JSON 
    (especialmente em 'descricaoGeral', 'observacoes', 'notas', etc.).
    Caso precise descrever alguma ação ou local, substitua os nomes específicos por termos totalmente neutros e genéricos 
    (ex: substitua 'Maria Lopes Ramos' por 'o paciente'; substitua 'Pronto Atendimento Dr. José Lins' por 'o pronto atendimento' ou 'a instituição emissora').
    Analise esta guia ou encaminhamento médico em português e extraia as seguintes informações no formato JSON:
    {
      "especialidadeDestino": "Especialidade médica de destino/especialista solicitado (ex: Endocrinologista, Cardiologista)",
      "instituicaoDestino": "Serviço, hospital, UBS ou ambulatório de destino, se houver",
      "motivo": "Hipótese diagnóstica ou diagnóstico confirmado (incluindo código CID, se houver) que justifica o encaminhamento",
      "resumoCaso": "Resumo do caso: sintomas, exames relevantes já feitos, tratamentos já tentados e medicamentos em uso",
      "objetivo": "Objetivo do encaminhamento (ex: 'Avaliação diagnóstica', 'Definição de conduta cirúrgica', 'Acompanhamento compartilhado')",
      "prioridade": "Nível de prioridade ou classificação de risco (Emergência, Urgência, Prioritário, Eletivo/Rotina) se houver, senão null",
      "dataEmissao": "Data de emissão do encaminhamento médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do encaminhamento médico"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional. 
    """
)
@RegisterAiService(modelName = "encaminhamento")
public interface EncaminhamentoIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Retry
    @CircuitBreaker
    @Override
    Encaminhamento extractData(Image image);
}
