package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class LipidogramaExame extends Exame {
    private Double colesterolTotal;
    private Double triglicerideos;
    private Double colesterolHdl;
    private Double colesterolLdl;
    private Double colesterolVldl;
}
