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
public class InboxDocumentProcessingRequestReprocessor {

    private static final Logger logger = LoggerFactory.getLogger(InboxDocumentProcessingRequestReprocessor.class);

    private final ProcessDocumentRequestService processDocumentRequestService;
    private final InboxDocumentProcessingRequestMapper mapper;
    private final InboxDocumentProcessingRequestRepository inboxRepository;


    @Inject
    public InboxDocumentProcessingRequestReprocessor(ProcessDocumentRequestService processDocumentRequestService,
                                                     InboxDocumentProcessingRequestMapper mapper,
                                                     InboxDocumentProcessingRequestRepository inboxRepository) {
        this.processDocumentRequestService = processDocumentRequestService;
        this.mapper = mapper;
        this.inboxRepository = inboxRepository;
    }

    @Blocking
    @Scheduled(every = "${app.inbox.reprocessor.interval:10s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void process() {
        try {
            var reprocessInbox = inboxRepository.findAllReprocess();
            reprocessInbox.stream().map(mapper::toDomain).forEach(processDocumentRequestService::process);
        } catch (Exception e) {
            logger.error("action=reprocessInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
