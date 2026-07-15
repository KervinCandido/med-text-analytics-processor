package br.com.fiap.techchallenge.processor.domain.laudo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MamografiaLaudo extends Laudo {
    private String indicacaoClinica;     // Rastreamento ou Diagnóstico
    private String composicaoDensidade; // Composição e Densidade Cútaneo-Glandular (Tipo A, B, C ou D)
    private String descricaoAchados;    // Descrição dos Achados (Análise Comparativa)
    private String recomendacaoClinica; // Recomendação clínica/orientação de seguimento
    private String categoriaBirads;     // Classificação Categoria BI-RADS (ex: BI-RADS 2)
}
