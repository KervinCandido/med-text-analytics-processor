package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class HemoglobinaGlicadaExame extends Exame {
    private Double hemoglobinaGlicada;
    private String valoresReferencia;
    private Double glicemiaMediaEstimada;
}
