package br.com.fiap.techchallenge.processor.persistence.entity.exame;

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
public class LeucogramaEntity {
    private Double leucocitosTotais;
    
    // Neutrófilos
    private Double neutrofilosRelativo;
    private Double neutrofilosAbsoluto;
    
    private Double neutrofilosSegmentadosRelativo;
    private Double neutrofilosSegmentadosAbsoluto;
    
    private Double neutrofilosBastoesRelativo;
    private Double neutrofilosBastoesAbsoluto;
    
    // Linfócitos
    private Double linfocitosRelativo;
    private Double linfocitosAbsoluto;
    
    // Monócitos
    private Double monocitosRelativo;
    private Double monocitosAbsoluto;
    
    // Eosinófilos
    private Double eosinofilosRelativo;
    private Double eosinofilosAbsoluto;
    
    // Basófilos
    private Double basofilosRelativo;
    private Double basofilosAbsoluto;
}
