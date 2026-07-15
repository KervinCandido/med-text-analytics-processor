package br.com.fiap.techchallenge.processor.service.ia.receita;

import br.com.fiap.techchallenge.processor.persistence.entity.receita.ReceitaEntity;
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
    Analise esta receita médica em português e extraia as informações estruturadas no seguinte formato JSON:
    {
      "dataReceita": "Data da receita (escrita ou impressa) no formato ISO-8601 (ex: YYYY-MM-DDTHH:mm:ss). Se houver apenas data, use HH:mm:ss padrão 00:00:00.",
      "itens": [
        {
          "nomeMedicamento": "Nome comercial ou princípio ativo (genérico)",
          "formaFarmaceutica": "Forma de apresentação (ex: Comprimido, Gotas, Xarope, Pomada, Injetável)",
          "concentracao": "Dosagem/Concentração do medicamento (ex: 500mg, 10mg/ml)",
          "quantidadeTotal": "Quantidade total a ser dispensada (ex: 2 caixas, 1 frasco) OU expressões indicando tratamento contínuo/indeterminado (ex: 'Uso Contínuo', 'Período Indeterminado') quando a quantidade física exata não for declarada.",
          "posologiaOrientacoes": "Instruções de uso/orientações do paciente (ex: Tomar 1 comprimido de 8 em 8 horas por 7 dias)"
        }
      ],
      "observacoes": ["Observação 1"], // Lista de observações clínicas, senão houver retorne array vazio
      "notas": ["Nota de rodapé 1"], // Lista de notas de rodapé ou observações técnicas, senão houver retorne array vazio
      "descricaoGeral": "Resumo geral da receita"
    }
    Atenção importante: Caligrafias médicas podem ser de difícil leitura. Faça o melhor esforço possível utilizando o contexto clínico dos medicamentos, dosagens padrão e associações comuns de posologia para decifrar e transcrever corretamente as palavras.
    Se algum valor ou data não for encontrado ou não estiver legível, retorne null para ele.
    Retorne apenas o JSON, sem markdown ou texto adicional.
    """
)
@RegisterAiService(modelName = "receita")
public interface ReceitaIAService extends DocumentExtractDataIAService {
    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias preenchendo os campos solicitados.
        """
    )
    @Override
    ReceitaEntity extractData(Image image);
}
