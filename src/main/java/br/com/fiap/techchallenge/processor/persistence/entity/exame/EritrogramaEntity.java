package br.com.fiap.techchallenge.processor.persistence.entity.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class EritrogramaEntity {
    private Double hemacias;
    private Double hemoglobina;
    private Double hematocrito;
    private Double vcm;
    private Double hcm;
    private Double chcm;
    private Double rdw;
}
