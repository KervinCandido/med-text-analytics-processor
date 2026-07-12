package br.com.fiap.techchallenge.processor.domain.inbox;

import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@MongoEntity(collection = "inbox")
public class InboxEvent extends PanacheMongoEntity {

    private String inboxId;
    private List<String> filePaths;
    private String userId;
    private InboxEventStatus status;
    private int totalFiles;
    private String errorDetail;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public static List<InboxEvent> findAllPending() {
        return list("status", InboxEventStatus.PENDING);
    }

    public void processing() {
        if (InboxEventStatus.PENDING.equals(status)) {
           this.status = InboxEventStatus.PROCESSING;
        }
    }

    public void processed() {
        if (InboxEventStatus.PROCESSING.equals(status)) {
            this.status = InboxEventStatus.PROCESSED;
            this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        }
    }
}
