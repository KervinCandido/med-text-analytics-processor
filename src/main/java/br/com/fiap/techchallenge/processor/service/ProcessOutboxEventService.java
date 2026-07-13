package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.outbox.OutboxEvent;
import br.com.fiap.techchallenge.processor.publisher.DataExtractedPublisher;
import io.smallrye.faulttolerance.api.ExponentialBackoff;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class ProcessOutboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInboxEventService.class);

    private final DataExtractedPublisher dataExtractedPublisher;

    @Inject
    public ProcessOutboxEventService(DataExtractedPublisher dataExtractedPublisher) {
        this.dataExtractedPublisher = dataExtractedPublisher;
    }

    @Retry(
            maxRetries = 5,
            delay = 5,
            delayUnit = ChronoUnit.SECONDS,
            jitter = 150,
            jitterDelayUnit = ChronoUnit.MILLIS
    )
    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.4,
            delay = 30,
            delayUnit = ChronoUnit.SECONDS
    )
    @Fallback(fallbackMethod = "processFallback")
    @ExponentialBackoff
    public void process(OutboxEvent outboxEvent) {
        logger.info("action=processOutboxEvent, outboxEventId={}", outboxEvent.getOutboxId());
        outboxEvent.getDocuments().forEach(dataExtractedPublisher::publish);
    }

    public void processFallback(OutboxEvent outboxEvent) {
        outboxEvent.failed();
        outboxEvent.persistOrUpdate();
    }
}
