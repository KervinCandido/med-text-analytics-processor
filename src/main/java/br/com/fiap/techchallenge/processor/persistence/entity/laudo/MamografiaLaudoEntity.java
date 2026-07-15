package br.com.fiap.techchallenge.processor.persistence.entity.laudo;

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
public class MamografiaLaudoEntity extends LaudoEntity {
    private String indicacaoClinica;     // Rastreamento ou Diagnóstico
    private String composicaoDensidade; // Composição e Densidade Cútaneo-Glandular (Tipo A, B, C ou D)
    private String descricaoAchados;    // Descrição dos Achados (Análise Comparativa)
    private String recomendacaoClinica; // Recomendação clínica/orientação de seguimento
    private String categoriaBirads;     // Classificação Categoria BI-RADS (ex: BI-RADS 2)
}
