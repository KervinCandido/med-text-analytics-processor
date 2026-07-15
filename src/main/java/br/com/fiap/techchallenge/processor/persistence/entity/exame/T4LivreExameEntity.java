package br.com.fiap.techchallenge.processor.persistence.entity.exame;

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
public class T4LivreExameEntity extends ExameEntity {
    private Double t4Livre;
    private String valoresReferencia;
}
