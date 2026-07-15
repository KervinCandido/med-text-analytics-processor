package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.domain.Documento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataExtractedPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DataExtractedPublisher.class);

    private final Emitter<Documento> emitter;

    @Inject
    public DataExtractedPublisher(@Channel("document-processed-response") Emitter<Documento> emitter) {
        this.emitter = emitter;
    }

    public void publish(Documento document) {
        emitter.send(document);
        logger.info("action=publishDocumentProcessedEvent");
    }
}
