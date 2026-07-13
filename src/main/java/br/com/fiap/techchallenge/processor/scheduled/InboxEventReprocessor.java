package br.com.fiap.techchallenge.processor.scheduled;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.service.ProcessInboxEventService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class InboxEventReprocessor {

    private static final Logger logger = LoggerFactory.getLogger(InboxEventReprocessor.class);

    private final ProcessInboxEventService processInboxEventService;

    @Inject
    public InboxEventReprocessor(ProcessInboxEventService processInboxEventService) {
        this.processInboxEventService = processInboxEventService;
    }

    @Blocking
    @Scheduled(every = "${app.inbox.reprocessor.interval:10s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void process() {
        try {
            List<InboxEvent> pendingEvents = InboxEvent.findAllReprocess();
            pendingEvents.forEach(processInboxEventService::process);
        } catch (Exception e) {
            logger.error("action=reprocessInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
