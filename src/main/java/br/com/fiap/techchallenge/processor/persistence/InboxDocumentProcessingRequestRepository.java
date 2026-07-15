package br.com.fiap.techchallenge.processor.persistence;

import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface InboxEventRepository extends PanacheMongoRepository<InboxDocumentProcessingRequestEntity> {}
