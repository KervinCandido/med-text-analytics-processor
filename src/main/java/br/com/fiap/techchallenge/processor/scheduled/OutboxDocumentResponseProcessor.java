package br.com.fiap.techchallenge.processor.scheduled;

import br.com.fiap.techchallenge.processor.persistence.OutboxDocumentProcessedResponseRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxEventEntity;
import br.com.fiap.techchallenge.processor.service.ProcessOutboxEventService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class OutboxEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventProcessor.class);

    private final ProcessOutboxEventService processOutboxEventService;
    private final OutboxDocumentProcessedResponseRepository outboxRepository;

    @Inject
    public OutboxEventProcessor(ProcessOutboxEventService processOutboxEventService,
                                OutboxDocumentProcessedResponseRepository outboxRepository) {
        this.processOutboxEventService = processOutboxEventService;
        this.outboxRepository = outboxRepository;
    }

    @Blocking
    @Scheduled(every = "${app.inbox.processor.interval:10s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void process() {
        try {
            List<OutboxEventEntity> pendingEvents = outboxRepository.findAllPending();
            pendingEvents.forEach(processOutboxEventService::process);
        } catch (Exception e) {
            logger.error("action=processInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
