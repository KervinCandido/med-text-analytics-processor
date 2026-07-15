package br.com.fiap.techchallenge.processor.persistence.entity.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class TshExameEntity extends ExameEntity {
    private Double tshBasal;
    private String valoresReferencia;
    private String notaReferenciaGestantes;
}
