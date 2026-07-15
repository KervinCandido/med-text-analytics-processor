package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

import static br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest.RETRY_LIMIT;

@ApplicationScoped
public class InboxDocumentProcessingRequestRepository implements PanacheMongoRepository<InboxDocumentProcessingRequestEntity> {

    public List<InboxDocumentProcessingRequestEntity> findAllPending() {
        return list("status", ProcessingStatus.PENDING);
    }

    public List<InboxDocumentProcessingRequestEntity> findAllReprocess() {
        return list("status = ?1 AND retryCount <= ?2", ProcessingStatus.FAILED, RETRY_LIMIT);
    }
}
