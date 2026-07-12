package br.com.fiap.techchallenge.processor.domain.outbox;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;

@MongoEntity(collection = "processor_outbox")
public class ProcessorOutboxEvent extends PanacheMongoEntity {
    public String jobId;
    public String payload; // JSON representation of JobStatusUpdateDTO
    public String status; // PENDING, PROCESSED, FAILED
    public LocalDateTime createdAt;
}
