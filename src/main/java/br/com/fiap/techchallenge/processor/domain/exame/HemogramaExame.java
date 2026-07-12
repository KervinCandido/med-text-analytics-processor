package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class HemogramaExame extends Exame {
    private Eritrograma eritrograma;
    private Leucograma leucograma;
    private Plaquetograma plaquetograma;
}
