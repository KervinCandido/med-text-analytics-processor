package br.com.fiap.techchallenge.processor.persistence.entity.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class LipidogramaExameEntity extends ExameEntity {
    private Double colesterolTotal;
    private Double triglicerideos;
    private Double colesterolHdl;
    private Double colesterolLdl;
    private Double colesterolVldl;
}
