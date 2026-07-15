package br.com.fiap.techchallenge.processor.service.ia.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.ColonoscopiaLaudoEntity;
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
    Analise este laudo de exame de Colonoscopia em português e extraia as seguintes informações no formato JSON:
    {
      "tecnica": "Dados técnicos, preparo e sedação do exame. Inclua drogas injetáveis utilizadas para sedação/analgesia (ex: Propofol, Fentanil, Midazolam), qualidade do preparo intestinal (ex: classificação excelente/adequada, Escala de Boston) e a extensão do exame (se ceco foi atingido ou se foi interrompido)",
      "analiseSegmentar": "Descrição detalhada da mucosa, calibre e padrão vascular de cada região/segmento do intestino percorrido (íleo terminal, ceco/cólon ascendente, cólon transverso, cólon descendente/sigmoide, reto e canal anal)",
      "descricaoLesoes": "Descrição detalhada de qualquer lesão/irregularidade (pólipos ou tumores) encontrada. Inclua localização segmentar, tamanho/quantidade e aspecto morfológico (se plana, elevada, pediculada, incluindo Classificação de Paris)",
      "procedimentosAdicionais": "Qualquer intervenção realizada no exame, como biópsias, polipectomia, mucosectomia e a respectiva identificação de frascos de envio (ex: Frasco 1: Pólipo de cólon sigmoide)",
      "impressaoDiagnostica": "Conclusão ou impressão diagnóstica final do exame (ex: doença diverticular, polipectomia, pancolite, exame dentro dos padrões de normalidade)",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo de colonoscopia"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-colonoscopia")
public interface LaudoColonoscopiaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    ColonoscopiaLaudoEntity extractData(Image image);
}
