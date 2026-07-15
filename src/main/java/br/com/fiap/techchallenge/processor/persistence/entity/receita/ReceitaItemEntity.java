package br.com.fiap.techchallenge.processor.persistence.entity.receita;

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
public class ReceitaItemEntity {
    private String nomeMedicamento;
    private String formaFarmaceutica;
    private String concentracao;
    private String quantidadeTotal;
    private String posologiaOrientacoes;
}
