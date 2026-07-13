package br.com.fiap.techchallenge.processor.domain.outbox;

import br.com.fiap.techchallenge.processor.domain.Document;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@MongoEntity(collection = "outbox")
public class OutboxEvent extends PanacheMongoEntity {

    private String outboxId;
    private OutboxEventStatus status; // PENDING, PROCESSED, FAILED
    private LocalDateTime createdAt;
    private List<Document> documents;

    public OutboxEvent() {
        this.outboxId = UUID.randomUUID().toString();
        this.status = OutboxEventStatus.PENDING;
        this.documents = new ArrayList<>();
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public static List<InboxEvent> findAllPending() {
        return list("status", OutboxEventStatus.PENDING);
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }

    public void failed() {
        this.status = OutboxEventStatus.FAILED;
    }
}
