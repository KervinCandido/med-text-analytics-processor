package br.com.fiap.techchallenge.processor.config;

import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MongoIndexesInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    MongoIndexesInitializer.class
            );

    private final InboxDocumentProcessingRequestRepository
            inboxRepository;

    @Inject
    public MongoIndexesInitializer(
            InboxDocumentProcessingRequestRepository
                    inboxRepository
    ) {
        this.inboxRepository = inboxRepository;
    }

    void onStart(
            @Observes StartupEvent event
    ) {
        String indexName =
                inboxRepository.ensureEventIdUniqueIndex();

        logger.info(
                "action=ensureMongoIndex, "
                        + "collection=inbox, indexName={}, "
                        + "field=eventId, unique=true",
                indexName
        );
    }
}
