package br.com.fiap.techchallenge.processor.persistence.entity;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@BsonDiscriminator
@MongoEntity(collection = "documentos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class DocumentoEntity {

    @BsonId
    private ObjectId id;
    private UUID patientId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
}
