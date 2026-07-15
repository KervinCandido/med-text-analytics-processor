package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class T4LivreExame extends Exame {
    private Double t4Livre;
    private String valoresReferencia;
}
