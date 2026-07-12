package br.com.fiap.techchallenge.processor.service.ia.classify;

import br.com.fiap.techchallenge.processor.dto.DocumentMetaDataDTO;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;

@SystemMessage(
        """
        Analise esta imagem médica. Identifique todos os documentos ou tipos de exames que aparecem na imagem.
        Uma única imagem pode conter mais de um exame. Classifique-os gerando uma lista com as categorias correspondentes:
        - EXAME_HEMOGRAMA (se contiver hemograma completo)
        - EXAME_LIPIDOGRAMA (se contiver perfil lipídico / lipidograma)
        - EXAME_GLICEMIA_JEJUM (se contiver exame de glicemia de jejum)
        - EXAME_HEMOGLOBINA_GLICADA (se contiver exame de hemoglobina glicada / HbA1c)
        - EXAME_TSH (se contiver exame de TSH / Hormônio Tireoestimulante)
        - EXAME_T4_LIVRE (se contiver exame de T4 Livre ou Tiroxina Livre)
        - EXAME_BETA_HCG (se contiver exame de Beta-HCG / Teste de Gravidez)
        - EXAME_OUTROS (outro tipo de exame laboratorial ou de imagem não listado acima)
        - RECEITA (receituário médico de medicamentos)
        - LAUDO_UST_ABDOME_TOTAL (laudo de ultrassonografia do abdome total)
        - LAUDO_US_OBSTETRICO_ENDOVAGINAL (laudo de ultrassom obstétrico endovaginal)
        - LAUDO_US_OBSTETRICO_DOPPLER (laudo de ultrassom obstétrico com doppler)
        - LAUDO_US_PELVICA_TRANSVAGINAL (laudo de ultrassom pélvico transvaginal)
        - LAUDO_RESSONANCIA_MAGNETICA (laudo de ressonância magnética de qualquer parte do corpo, ex: crânio, ombro, joelho, coluna)
        - LAUDO_TOMOGRAFIA_COMPUTADORIZADA (laudo de tomografia computadorizada de qualquer parte do corpo, ex: tórax, abdome, crânio)
        - LAUDO_MAMOGRAFIA (laudo de mamografia / radiologia mamária)
        - LAUDO_ENDOSCOPIA_DIGESTIVA_ALTA (laudo de endoscopia digestiva alta)
        - LAUDO_COLONOSCOPIA (laudo de colonoscopia)
        - LAUDO_OUTROS (outros laudos médicos de exames ou cirurgias não listados acima - NÃO classifique folhas de resultados de exames de laboratório comuns como laudo apenas por conter assinaturas ou identificações)
        - RELATORIO (relatório médico ou evolução clínica)
        - ENCAMINHAMENTO (guia de encaminhamento para outro profissional)
        - REGISTRO_ATENDIMENTO (registro de atendimento em pronto-socorro ou ficha de consulta)
        - OUTROS (caso não seja possível identificar ou se não for um documento médico)
        Atenção especial: O exame "Tiroxina Livre" deve ser classificado as "EXAME_T4_LIVRE". Não duplique a classificação se houver informações de cabeçalhos de laboratório.
        Retorne o resultado exatamente no seguinte formato JSON, contendo um array de classificações, sem markdown ou texto adicional:
        {"classi"fications": ["CLASSIFICACAO_1", "CLASSIFICACAO_2"]}
        """
)
@RegisterAiService(modelName = "classify-document")
public interface ClassifyDocumentIAService {

    @UserMessage(
        """
        Analise a imagem do documento fornecida.
        Extraia as informações necessárias e classifique o documento preenchendo os campos solicitados.
        """
    )
    @Retry
    @CircuitBreaker
    DocumentMetaDataDTO classifyDocument(Image image);
}
