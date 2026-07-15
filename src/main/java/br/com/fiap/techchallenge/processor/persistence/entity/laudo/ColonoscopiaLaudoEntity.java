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
public class ColonoscopiaLaudoEntity extends LaudoEntity {
    private String analiseSegmentar;        // Análise Segmentar do Intestino (íleo terminal, cólons, reto, canal anal)
    private String descricaoLesoes;         // Descrição de Lesões (localização, tamanho, quantidade, aspecto/Paris)
    private String procedimentosAdicionais; // Biópsias, polipectomias, mucosectomias, identificação de frascos
}
