package br.com.fiap.techchallenge.processor.domain.outbox;

import br.com.fiap.techchallenge.processor.domain.Document;
import br.com.fiap.techchallenge.processor.util.Constants;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@MongoEntity(collection = "outbox")
public class OutboxEvent extends PanacheMongoEntity {

    private UUID outboxId;
    private OutboxEventStatus status; // PENDING, PROCESSED, FAILED
    private LocalDateTime createdAt;
    private List<Document> documents;

    public OutboxEvent() {
        this.outboxId = UUID.randomUUID();
        this.status = OutboxEventStatus.PENDING;
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }
}
