package br.com.fiap.techchallenge.processor.scheduled;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.service.ProcessInboxEventService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class InboxEventProcessor {

    private static final Logger logger = LoggerFactory.getLogger(InboxEventProcessor.class);

    private final ProcessInboxEventService processInboxEventService;

    @Inject
    public InboxEventProcessor(ProcessInboxEventService processInboxEventService) {
        this.processInboxEventService = processInboxEventService;
    }

    @Scheduled(every = "${app.inbox.processor.interval:5s}")
    public void process() {
        try {
            List<InboxEvent> pendingEvents = InboxEvent.findAllPending();
            pendingEvents.parallelStream().forEach(processInboxEventService::process);
        } catch (Exception e) {
            logger.error("action=processInboxEventsError, reason={}", e.getMessage(), e);
        }
    }
}
