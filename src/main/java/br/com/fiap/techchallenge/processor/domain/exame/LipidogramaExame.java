package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class LipidogramaExame extends Exame {
    private Double colesterolTotal;
    private Double triglicerideos;
    private Double colesterolHdl;
    private Double colesterolLdl;
    private Double colesterolVldl;
}
