package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class TshExame extends Exame {
    private Double tshBasal;
    private String valoresReferencia;
    private String notaReferenciaGestantes;
}
