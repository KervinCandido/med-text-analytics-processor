package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class T4LivreExame extends Exame {
    private Double t4Livre;
    private String valoresReferencia;
}
