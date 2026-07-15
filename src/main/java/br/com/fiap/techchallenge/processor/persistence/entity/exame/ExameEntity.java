package br.com.fiap.techchallenge.processor.persistence.entity.exame;

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
public class ExameEntity extends DocumentoEntity {
    private String exameTipo; // e.g. "HEMOGRAMA", "LIPIDOGRAMA", "OUTRO"
    private String material;  // e.g. "Soro", "Sangue total"
    private String metodo;    // e.g. "Enzimático", "Automatizado"
    private LocalDateTime dataColeta;     // Data/hora da coleta do exame
    private LocalDateTime dataLiberacao;  // Data/hora de liberação do resultado
    private List<String> observacoes; // Lista de observações do exame
    private List<String> notas;       // Lista de notas do exame
    private String descricaoGeral;

}
