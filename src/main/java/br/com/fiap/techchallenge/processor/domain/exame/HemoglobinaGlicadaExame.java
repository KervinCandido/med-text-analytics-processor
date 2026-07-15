package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class HemoglobinaGlicadaExame extends Exame {
    private Double hemoglobinaGlicada;
    private String valoresReferencia;
    private Double glicemiaMediaEstimada;
}
