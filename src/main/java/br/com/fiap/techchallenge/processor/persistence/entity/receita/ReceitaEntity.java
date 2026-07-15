package br.com.fiap.techchallenge.processor.persistence.entity.receita;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.time.LocalDateTime;
import java.util.List;

@BsonDiscriminator
@MongoEntity(collection = "documentos")
@Getter
@Setter
@NoArgsConstructor
public class ReceitaEntity extends DocumentoEntity {
    private List<ReceitaItemEntity> itens;
    private List<String> observacoes;
    private List<String> notas;
    private LocalDateTime dataReceita;
    private String descricaoGeral;

}
