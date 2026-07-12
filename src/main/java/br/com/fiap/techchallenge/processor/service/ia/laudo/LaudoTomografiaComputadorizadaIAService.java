package br.com.fiap.techchallenge.processor.service.ia.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
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
    Analise este laudo de exame de Tomografia Computadorizada (TC) em português e extraia as seguintes informações no formato JSON:
    {
      "areaCorpo": "Região ou área do corpo analisada (ex: Tórax, Abdome Total, Crânio, Seios da Face)",
      "tecnica": "Técnica do exame, indicando a metodologia de aquisição de imagens e o uso de contraste iodado (ex: Exame realizado com cortes finos multiplanares helicoidais, sem e com administração de contraste iodado endovenoso)",
      "descricaoAnatomica": "Relato detalhado da morfologia, atenuação/densidade, contornos e aspecto anatômico das estruturas e órgãos analisados",
      "achadosNormais": "Confirmação das estruturas corporais que se apresentam normais e sem alterações estruturais ou de atenuação",
      "achadosPatologicos": "Descrição minuciosa de qualquer lesão, nódulo, consolidação pulmonar, derrame pleural, cisto, cálculo ou anomalia encontrada, incluindo as suas medidas e localização exata",
      "impressaoDiagnostica": "Síntese diagnóstica, hipótese, conclusões ou classificações padronizadas listadas no laudo",
      "dataLaudo": "Data do laudo médico no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss)",
      "observacoes": [],
      "notas": [],
      "descricaoGeral": "Resumo geral descritivo do laudo de tomografia computadorizada"
    }
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "laudo-tomografia-computadorizada")
public interface LaudoTomografiaComputadorizadaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Retry
    @CircuitBreaker
    @Override
    Laudo extractData(Image image);
}
