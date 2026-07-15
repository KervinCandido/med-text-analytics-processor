package br.com.fiap.techchallenge.processor.persistence.entity.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class T4LivreExameEntity extends ExameEntity {
    private Double t4Livre;
    private String valoresReferencia;
}
