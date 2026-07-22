package br.com.fiap.techchallenge.processor.persistence.entity.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MongoEntity(collection = "inbox")
public class InboxDocumentProcessingRequestEntity {

    @BsonId
    private ObjectId id;
    private UUID eventId;
    private UUID documentId;
    private UUID patientId;
    private String filePath;
    private String contentType;
    private ProcessingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Short retryCount;

    public InboxDocumentProcessingRequestEntity() {
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        this.status = ProcessingStatus.PENDING;
        this.retryCount = 0;
    }
}
