package br.com.fiap.techchallenge.processor.service.ia;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.service.ia.encaminhamento.EncaminhamentoIAService;
import br.com.fiap.techchallenge.processor.service.ia.exame.*;
import br.com.fiap.techchallenge.processor.service.ia.laudo.*;
import br.com.fiap.techchallenge.processor.service.ia.outros.OutrosIAService;
import br.com.fiap.techchallenge.processor.service.ia.receita.ReceitaIAService;
import br.com.fiap.techchallenge.processor.service.ia.registro_atendimento.RegistroAtendimentoIAService;
import br.com.fiap.techchallenge.processor.service.ia.relatorio.RelatorioIAService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DocumentExtractDataIAStrategy {

    @Inject EncaminhamentoIAService encaminhamentoIAService;
    @Inject BetaHcgIAService betaHcgIAService;
    @Inject ExameOutrosIAService exameOutrosIAService;
    @Inject GlicemiaJejumIAService glicemiaJejumIAService;
    @Inject HemoglobinaGlicadaIAService hemoglobinaGlicadaIAService;
    @Inject HemogramaIAService hemogramaIAService;
    @Inject LipidogramaIAService lipidogramaIAService;
    @Inject T4LivreIAService t4LivreIAService;
    @Inject TshIAService tshIAService;
    @Inject LaudoColonoscopiaIAService laudoColonoscopiaIAService;
    @Inject LaudoEndoscopiaDigestivaAltaIAService laudoEndoscopiaDigestivaAltaIAService;
    @Inject LaudoMamografiaIAService laudoMamografiaIAService;
    @Inject LaudoOutrosIAService laudoOutrosIAService;
    @Inject LaudoRessonanciaMagneticaIAService laudoRessonanciaMagneticaIAService;
    @Inject LaudoTomografiaComputadorizadaIAService laudoTomografiaComputadorizadaIAService;
    @Inject LaudoUsObstetricoDopplerIAService laudoUsObstetricoDopplerIAService;
    @Inject LaudoUsObstetricoEndovaginalIAService laudoUsObstetricoEndovaginalIAService;
    @Inject LaudoUsPelvicaTransvaginalIAService laudoUsPelvicaTransvaginalIAService;
    @Inject LaudoUstAbdomeTotalIAService laudoUstAbdomeTotalIAService;
    @Inject OutrosIAService outrosIAService;
    @Inject ReceitaIAService receitaIAService;
    @Inject RegistroAtendimentoIAService registroAtendimentoIAService;
    @Inject RelatorioIAService relatorioIAService;

    public DocumentExtractDataIAService get(DocumentType classification) {
        return switch (classification) {
            case ENCAMINHAMENTO -> encaminhamentoIAService;
            case EXAME_BETA_HCG -> betaHcgIAService;
            case EXAME_OUTROS -> exameOutrosIAService;
            case EXAME_GLICEMIA_JEJUM -> glicemiaJejumIAService;
            case EXAME_HEMOGLOBINA_GLICADA -> hemoglobinaGlicadaIAService;
            case EXAME_HEMOGRAMA -> hemogramaIAService;
            case EXAME_LIPIDOGRAMA -> lipidogramaIAService;
            case EXAME_T4_LIVRE -> t4LivreIAService;
            case EXAME_TSH -> tshIAService;
            case LAUDO_COLONOSCOPIA -> laudoColonoscopiaIAService;
            case LAUDO_ENDOSCOPIA_DIGESTIVA_ALTA -> laudoEndoscopiaDigestivaAltaIAService;
            case LAUDO_MAMOGRAFIA -> laudoMamografiaIAService;
            case LAUDO_OUTROS -> laudoOutrosIAService;
            case LAUDO_RESSONANCIA_MAGNETICA -> laudoRessonanciaMagneticaIAService;
            case LAUDO_TOMOGRAFIA_COMPUTADORIZADA -> laudoTomografiaComputadorizadaIAService;
            case LAUDO_US_OBSTETRICO_DOPPLER -> laudoUsObstetricoDopplerIAService;
            case LAUDO_US_OBSTETRICO_ENDOVAGINAL -> laudoUsObstetricoEndovaginalIAService;
            case LAUDO_US_PELVICA_TRANSVAGINAL -> laudoUsPelvicaTransvaginalIAService;
            case LAUDO_UST_ABDOME_TOTAL -> laudoUstAbdomeTotalIAService;
            case OUTROS -> outrosIAService;
            case RECEITA -> receitaIAService;
            case REGISTRO_ATENDIMENTO -> registroAtendimentoIAService;
            case RELATORIO -> relatorioIAService;
            default -> throw new IllegalArgumentException("Unsupported classification: " + classification);
        };
    }
}
