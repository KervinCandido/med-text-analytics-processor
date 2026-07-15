package br.com.fiap.techchallenge.processor.persistence.entity.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@MongoEntity(collection = "inbox")
public class InboxEventEntity extends PanacheMongoEntity {

    public static final int RETRY_LIMIT = 3;

    private String eventId;
    private String documentId;
    private String patientId;
    private String filePath;
    private ProcessingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Short retryCount;

    public InboxEventEntity() {
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        this.status = ProcessingStatus.PENDING;
        this.retryCount = 0;
    }

    public static List<InboxEventEntity> findAllPending() {
        return list("status", ProcessingStatus.PENDING);
    }

    public static List<InboxEventEntity> findAllReprocess() {
        return list("status = ?1 AND retryCount <= ?2", ProcessingStatus.FAILED, RETRY_LIMIT);
    }

    public void processing() {
        if (ProcessingStatus.PENDING.equals(status) || ProcessingStatus.FAILED.equals(status) ) {
           this.status = ProcessingStatus.PROCESSING;
        }
    }

    public void processed() {
        if (ProcessingStatus.PROCESSING.equals(status)) {
            this.status = ProcessingStatus.PROCESSED;
            this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        }
    }

    public void failed() {
        if (RETRY_LIMIT < retryCount) {
            this.status = ProcessingStatus.ALL_RETRY_FAILED;
        } else if (ProcessingStatus.PROCESSING.equals(status)) {
            this.status = ProcessingStatus.FAILED;
            this.retryCount++;
        }
        this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public void reprocessByRateLimit() {
        this.status = ProcessingStatus.PENDING;
    }
}
