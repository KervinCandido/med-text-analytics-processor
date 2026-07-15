package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class GlicemiaJejumExame extends Exame {
    private Double glicose;
    private String valoresReferencia;
    private String tempoJejum;
}
