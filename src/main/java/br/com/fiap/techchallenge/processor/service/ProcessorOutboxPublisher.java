package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.outbox.ProcessorOutboxEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Properties;

//@ApplicationScoped
public class ProcessorOutboxPublisher {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorOutboxPublisher.class);

    @ConfigProperty(name = "app.kafka.bootstrap-servers", defaultValue = "localhost:9092")
    String bootstrapServers;

    private KafkaProducer<String, String> producer;

    @PostConstruct
    void init() {
        logger.info("action=initProcessorOutboxPublisher, bootstrapServers={}", bootstrapServers);
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
    }

    @PreDestroy
    void destroy() {
        logger.info("action=destroyProcessorOutboxPublisher");
        if (producer != null) {
            producer.close();
        }
    }

//    @Scheduled(every = "${app.outbox.publisher.interval:5s}")
    public void publishEvents() {
        List<ProcessorOutboxEvent> pendingEvents = ProcessorOutboxEvent.list("status", "PENDING");
        if (!pendingEvents.isEmpty()) {
            logger.info("action=publishProcessorEventsStart, pendingCount={}", pendingEvents.size());
        }
        for (ProcessorOutboxEvent event : pendingEvents) {
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>("job-status-updates", event.jobId, event.payload);
                producer.send(record).get(); // blocking wait
                
                event.status = "PROCESSED";
                event.update();
                logger.info("action=publishProcessorEventSuccess, jobId={}, outboxId={}", event.jobId, event.id);
            } catch (Exception e) {
                logger.error("action=publishProcessorEventError, jobId={}, outboxId={}, reason={}", event.jobId, event.id, e.getMessage(), e);
                event.status = "FAILED";
                event.update();
            }
        }
    }
}
