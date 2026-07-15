package br.com.fiap.techchallenge.processor.persistence.entity.laudo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "laudos")
@Getter
@Setter
@NoArgsConstructor
public class EndoscopiaLaudoEntity extends LaudoEntity {
    private String analiseEsofago;          // Calibre, mucosa, transição, pinçamento diafragmático
    private String analiseEstomago;         // Forma, conteúdo, mucosa por regiões, retroflexão
    private String analiseDuodeno;          // Bulbo e segunda porção
    private String procedimentosAdicionais; // Biópsias, teste de urease, intervenções
}
