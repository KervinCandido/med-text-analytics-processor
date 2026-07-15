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
    Analise este laudo de exame de Ressonância Magnética em português e extraia as seguintes informações no formato JSON:
    {
      "areaCorpo": "Área ou região do corpo analisada (ex: Crânio, Ombro, Coluna Lombar, Joelho, Abdome)",
      "tecnica": "Técnica do exame, incluindo as sequências de pulso utilizadas, planos anatômicos e se houve uso de contraste (ex: Sequências multiplanares ponderadas em T1 e T2, com ou sem contraste paramagnético gadolínio)",
      "descricaoAnatomica": "Relato detalhado sobre a morfologia, contornos, sinal e aspecto dos tecidos, ossos e estruturas anatômicas da região analisada",
      "achadosNormais": "Confirmação das estruturas que se encontram normais e sem alterações de sinal ou morfologia",
      "achadosPatologicos": "Descrição minuciosa de qualquer lesão, herniação, rotura de tendão, edema ósseo, nódulo, tumor ou anomalia encontrada, incluindo as suas medidas e localização exata",
      "impressaoDiagnostica": "Síntese diagnóstica, hipótese ou conclusão final do laudo da ressonância magnética",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo de ressonância magnética"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-ressonancia-magnetica")
public interface LaudoRessonanciaMagneticaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    LaudoEntity extractData(Image image);
}
