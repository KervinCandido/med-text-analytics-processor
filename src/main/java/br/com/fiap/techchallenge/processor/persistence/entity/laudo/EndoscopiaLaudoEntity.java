package br.com.fiap.techchallenge.processor.persistence.entity.laudo;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator
@MongoEntity(collection = "documentos")
@Getter
@Setter
@NoArgsConstructor
public class EndoscopiaLaudoEntity extends LaudoEntity {
    private String analiseEsofago;          // Calibre, mucosa, transição, pinçamento diafragmático
    private String analiseEstomago;         // Forma, conteúdo, mucosa por regiões, retroflexão
    private String analiseDuodeno;          // Bulbo e segunda porção
    private String procedimentosAdicionais; // Biópsias, teste de urease, intervenções
}
