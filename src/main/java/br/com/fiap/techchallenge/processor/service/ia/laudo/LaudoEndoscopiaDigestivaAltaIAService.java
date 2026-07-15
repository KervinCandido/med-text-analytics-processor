package br.com.fiap.techchallenge.processor.service.ia.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.EndoscopiaLaudoEntity;
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
    Analise este laudo de exame de Endoscopia Digestiva Alta (EDA) em português e extraia as seguintes informações no formato JSON:
    {
      "tecnica": "Dados técnicos e medicamentosos do exame. Inclua o modelo do aparelho (videoendoscópio), sedação/anestesia utilizada (ex: Propofol, Midazolam, Lidocaína spray) e qualidade do exame/tolerância do paciente",
      "analiseEsofago": "Análise detalhada do esôfago. Inclua informações de calibre/luz, mucosa (ex: esofagite, úlceras, varizes), transição esôfago-gástrica/linha Z e pinçamento diafragmático (presença/ausência de hérnia de hiato)",
      "analiseEstomago": "Análise detalhada do estômago. Inclua forma/contratilidade, luz/conteúdo (normal, presença de bile/sangue), mucosa por regiões (fundo, corpo e antro) e manobra de retroflexão/retrovisão",
      "analiseDuodeno": "Análise detalhada do bulbo duodenal e segunda porção (mucosa e vilosidades)",
      "procedimentosAdicionais": "Qualquer procedimento adicional realizado, como biópsias (região coletada), teste de urease para H. pylori (positivo/negativo) ou intervenções terapêuticas (retirada de pólipos, cauterização)",
      "impressaoDiagnostica": "Conclusão ou impressão diagnóstica final resumindo os achados (ex: gastrite enantematosa, esofagite, hérnia de hiato)",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo de endoscopia digestiva alta"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-endoscopia-digestiva-alta")
public interface LaudoEndoscopiaDigestivaAltaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    EndoscopiaLaudoEntity extractData(Image image);
}
