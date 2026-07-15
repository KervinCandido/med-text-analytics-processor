package br.com.fiap.techchallenge.processor.service.ia.relatorio;

import br.com.fiap.techchallenge.processor.persistence.entity.relatorio.RelatorioEntity;
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
    Analise este relatório médico em português e extraia as seguintes informações no formato JSON:
    {
      "historicoClinico": "Histórico clínico e evolução da doença. Descreva sintomas iniciais, tempo de evolução da condição e tratamentos prévios realizados (cirurgias, internações, medicamentos)",
      "diagnostico": "Diagnóstico atual. Descrição clara da patologia ou lesão identificada",
      "cid": "Código CID (Classificação Internacional de Donenças), se estiver explícito no documento, senão null",
      "estadoAtual": "Estado atual do paciente. Descreva limitações funcionais, sintomas persistentes, estabilidade ou limitações físicas/cognitivas relativas ao exame físico e exames complementares",
      "condutaMedica": "Conduta médica atual. Descreva medicamentos prescritos com dosagens e terapias em andamento",
      "prognostico": "Estimativa de evolução ou recuperação da doença (ex: crônica, degenerativa, reversível)",
      "finalidade": "Finalidade declarada do relatório se houver (ex: 'Para fins de perícia previdenciária', 'Para fins de viagem aérea', 'Para adaptação escolar'), senão null",
      "dataRelatorio": "Data de emissão do relatório médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do relatório médico"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "relatorio")
public interface RelatorioIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    RelatorioEntity extractData(Image image);
}
