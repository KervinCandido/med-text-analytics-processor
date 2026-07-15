package br.com.fiap.techchallenge.processor.scheduled;

import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import br.com.fiap.techchallenge.processor.service.ProcessDocumentRequestService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InboxDocumentRequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(InboxDocumentRequestProcessor.class);

    private final InboxDocumentProcessingRequestRepository inboxRepository;
    private final InboxDocumentProcessingRequestMapper mapper;
    private final ProcessDocumentRequestService processDocumentRequestService;

    @Inject
    public InboxDocumentRequestProcessor(InboxDocumentProcessingRequestRepository inboxRepository,
                                         InboxDocumentProcessingRequestMapper mapper,
                                         ProcessDocumentRequestService processDocumentRequestService) {
        this.inboxRepository = inboxRepository;
        this.mapper = mapper;
        this.processDocumentRequestService = processDocumentRequestService;
    }

    @Blocking
    @Scheduled(every = "${app.inbox.processor.interval:10s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void process() {
        try {
            var pendingEvents = inboxRepository.findAllPending();
            pendingEvents.stream().map(mapper::toDomain).forEach(processDocumentRequestService::process);
        } catch (Exception e) {
            logger.error("action=processInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
