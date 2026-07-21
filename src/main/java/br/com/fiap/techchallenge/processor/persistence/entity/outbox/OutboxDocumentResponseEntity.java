package br.com.fiap.techchallenge.processor.persistence.entity.outbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@MongoEntity(collection = "outbox")
public class OutboxDocumentResponseEntity {

    @BsonId
    private ObjectId outboxId;

    private ProcessingStatus status;
    private ProcessingStatus responseStatus;
    private UUID eventId;
    private UUID documentId;
    private UUID patientId;
    private LocalDateTime createdAt;
    private List<ObjectId> documents;
    private String errorDetail;

    public OutboxDocumentResponseEntity() {
        this.status = ProcessingStatus.PENDING;
        this.responseStatus = ProcessingStatus.PROCESSED;
        this.documents = new ArrayList<>();
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }
}
