package br.com.fiap.techchallenge.processor.domain.receita;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ReceitaItem {
    private String nomeMedicamento;
    private String formaFarmaceutica;
    private String concentracao;
    private String quantidadeTotal;
    private String posologiaOrientacoes;
}
