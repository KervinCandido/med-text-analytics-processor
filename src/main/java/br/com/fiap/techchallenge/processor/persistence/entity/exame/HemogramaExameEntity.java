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
public class HemogramaExameEntity extends ExameEntity {
    private EritrogramaEntity eritrograma;
    private LeucogramaEntity leucograma;
    private PlaquetogramaEntity plaquetograma;
}
