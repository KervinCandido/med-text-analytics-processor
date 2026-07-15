package br.com.fiap.techchallenge.processor.domain.laudo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ColonoscopiaLaudo extends Laudo {
    private String analiseSegmentar;        // Análise Segmentar do Intestino (íleo terminal, cólons, reto, canal anal)
    private String descricaoLesoes;         // Descrição de Lesões (localização, tamanho, quantidade, aspecto/Paris)
    private String procedimentosAdicionais; // Biópsias, polipectomias, mucosectomias, identificação de frascos
}
