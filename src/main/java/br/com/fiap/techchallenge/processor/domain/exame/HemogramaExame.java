package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class HemogramaExame extends Exame {
    private Eritrograma eritrograma;
    private Leucograma leucograma;
    private Plaquetograma plaquetograma;
}
