package br.com.fiap.techchallenge.processor.domain.inbox;

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
public class InboxEvent extends PanacheMongoEntity {

    public static final int RETRY_LIMIT = 3;
    private String eventId;
    private String documentId;
    private String patientId;
    private String filePath;
    private InboxEventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Short retryCount;

    public InboxEvent() {
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        this.status = InboxEventStatus.PENDING;
        this.retryCount = 0;
    }

    public static List<InboxEvent> findAllPending() {
        return list("status", InboxEventStatus.PENDING);
    }

    public static List<InboxEvent> findAllReprocess() {
        return list("status = ?1 AND retryCount <= ?2", InboxEventStatus.FAILED, RETRY_LIMIT);
    }

    public void processing() {
        if (InboxEventStatus.PENDING.equals(status) || InboxEventStatus.FAILED.equals(status) ) {
           this.status = InboxEventStatus.PROCESSING;
        }
    }

    public void processed() {
        if (InboxEventStatus.PROCESSING.equals(status)) {
            this.status = InboxEventStatus.PROCESSED;
            this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        }
    }

    public void failed() {
        if (RETRY_LIMIT < retryCount) {
            this.status = InboxEventStatus.ALL_RETRY_FAILED;
        } else if (InboxEventStatus.PROCESSING.equals(status)) {
            this.status = InboxEventStatus.FAILED;
            this.retryCount++;
        }
        this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public void reprocessByRateLimit() {
        this.status = InboxEventStatus.PENDING;
    }
}
