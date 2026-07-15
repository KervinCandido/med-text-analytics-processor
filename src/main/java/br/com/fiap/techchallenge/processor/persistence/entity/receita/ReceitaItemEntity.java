package br.com.fiap.techchallenge.processor.persistence.entity.receita;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
public class ReceitaItemEntity {
    private String nomeMedicamento;
    private String formaFarmaceutica;
    private String concentracao;
    private String quantidadeTotal;
    private String posologiaOrientacoes;
}
