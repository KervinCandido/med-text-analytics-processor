package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class BetaHcgExame extends Exame {
    private Double betaHcgQuantitativo;
    private String resultadoQualitativo;
    private String valoresReferencia;
    private String idadeGestacionalTabela;
}
