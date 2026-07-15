package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CreateInboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(CreateInboxEventService.class);

    private final InboxDocumentProcessingRequestMapper inboxDocumentProcessingRequestMapper;
    private final InboxDocumentProcessingRequestRepository inboxRepository;

    @Inject
    public CreateInboxEventService(InboxDocumentProcessingRequestMapper inboxDocumentProcessingRequestMapper,
                                   InboxDocumentProcessingRequestRepository inboxRepository) {
        this.inboxDocumentProcessingRequestMapper = inboxDocumentProcessingRequestMapper;
        this.inboxRepository = inboxRepository;
    }

    public void create(InboxDocumentProcessingRequest inbox) {
        var entity = inboxDocumentProcessingRequestMapper.toEntity(inbox);
        inboxRepository.persist(entity);
        logger.info("action=saveInboxEventSuccess, inboxEvent={}", inbox);
    }
}
