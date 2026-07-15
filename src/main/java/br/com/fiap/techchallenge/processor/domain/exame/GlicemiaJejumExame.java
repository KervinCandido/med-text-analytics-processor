package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GlicemiaJejumExame extends Exame {
    private Double glicose;
    private String valoresReferencia;
    private String tempoJejum;
}
