package br.com.fiap.techchallenge.processor.scheduled;

import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.mapper.outbox.OutboxDocumentResponseMapper;
import br.com.fiap.techchallenge.processor.service.ProcessOutboxEventService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OutboxDocumentResponseProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxDocumentResponseProcessor.class);

    private final ProcessOutboxEventService processOutboxEventService;
    private final OutboxDocumentProcessedResponseRepository outboxRepository;
    private final OutboxDocumentResponseMapper outboxMapper;

    @Inject
    public OutboxDocumentResponseProcessor(ProcessOutboxEventService processOutboxEventService,
                                           OutboxDocumentProcessedResponseRepository outboxRepository,
                                           OutboxDocumentResponseMapper outboxMapper) {
        this.processOutboxEventService = processOutboxEventService;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }

    @Blocking
    @Scheduled(every = "${app.inbox.processor.interval:10s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void process() {
        try {
            var pendingEvents = outboxRepository.findAllPending();
            pendingEvents.stream().map(outboxMapper::toDomain).forEach(processOutboxEventService::process);
        } catch (Exception e) {
            logger.error("action=processInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
