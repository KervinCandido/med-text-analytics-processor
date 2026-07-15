package br.com.fiap.techchallenge.processor.domain.laudo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EndoscopiaLaudo extends Laudo {
    private String analiseEsofago;          // Calibre, mucosa, transição, pinçamento diafragmático
    private String analiseEstomago;         // Forma, conteúdo, mucosa por regiões, retroflexão
    private String analiseDuodeno;          // Bulbo e segunda porção
    private String procedimentosAdicionais; // Biópsias, teste de urease, intervenções
}
