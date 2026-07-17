package br.com.fiap.techchallenge.processor.persistence.entity.registro_atendimento;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.time.LocalDateTime;

@BsonDiscriminator
@MongoEntity(collection = "documentos")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAtendimentoEntity extends DocumentoEntity {
    private String paciente;
    private String medico;
    private LocalDateTime dataAtendimento;
    private String motivoAtendimento;
    private String descricaoGeral;
}
