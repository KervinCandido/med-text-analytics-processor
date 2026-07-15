package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class HemoglobinaGlicadaExame extends Exame {
    private Double hemoglobinaGlicada;
    private String valoresReferencia;
    private Double glicemiaMediaEstimada;
}
