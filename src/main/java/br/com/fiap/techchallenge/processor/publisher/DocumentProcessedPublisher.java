package br.com.fiap.techchallenge.processor.publisher;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessedResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DocumentProcessedPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DocumentProcessedPublisher.class);

    private final Emitter<DocumentProcessedResponseDTO> emitter;

    @Inject
    public DocumentProcessedPublisher(@Channel("document-processed-response") Emitter<DocumentProcessedResponseDTO> emitter) {
        this.emitter = emitter;
    }

    public void publish(DocumentProcessedResponseDTO document) {
        emitter.send(document);
        logger.info("action=publishDocumentProcessedEvent, {}", document);
    }
}
