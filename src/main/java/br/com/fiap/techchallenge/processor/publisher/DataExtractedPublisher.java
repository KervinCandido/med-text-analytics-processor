package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.domain.Document;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataExtractedPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DataExtractedPublisher.class);

    private final Emitter<Document> emitter;

    @Inject
    public DataExtractedPublisher(@Channel("data-extracted") Emitter<Document> emitter) {
        this.emitter = emitter;
    }

    public void publish(Document document) {
        emitter.send(document);
        logger.info("action=publishDocumentProcessedEvent");
    }
}
