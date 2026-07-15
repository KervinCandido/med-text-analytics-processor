package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxDocumentResponseEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxEventStatusEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OutboxDocumentProcessedResponseRepository implements PanacheMongoRepository<OutboxDocumentResponseEntity> {

    public List<OutboxDocumentResponseEntity> findAllPending() {
        return list("status", OutboxEventStatusEntity.PENDING);
    }
}
