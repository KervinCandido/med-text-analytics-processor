package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import com.mongodb.client.model.IndexOptions;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest.RETRY_LIMIT;
import static com.mongodb.client.model.Indexes.ascending;

@ApplicationScoped
public class InboxDocumentProcessingRequestRepository
        implements PanacheMongoRepository<InboxDocumentProcessingRequestEntity> {

    public static final String EVENT_ID_UNIQUE_INDEX =
            "inbox_event_id_unique";

    public List<InboxDocumentProcessingRequestEntity>
    findAllPending() {
        return list(
                "status",
                ProcessingStatus.PENDING
        );
    }

    public List<InboxDocumentProcessingRequestEntity>
    findAllReprocess() {
        return list(
                "status = ?1 AND retryCount <= ?2",
                ProcessingStatus.FAILED,
                RETRY_LIMIT
        );
    }

    public Optional<InboxDocumentProcessingRequestEntity>
    findByEventId(UUID eventId) {
        return find(
                "eventId",
                eventId
        ).firstResultOptional();
    }

    public String ensureEventIdUniqueIndex() {
        return mongoCollection().createIndex(
                ascending("eventId"),
                new IndexOptions()
                        .name(EVENT_ID_UNIQUE_INDEX)
                        .unique(true)
        );
    }
}
