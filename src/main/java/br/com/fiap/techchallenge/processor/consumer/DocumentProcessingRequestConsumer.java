package br.com.fiap.techchallenge.processor.consumer;

import br.com.fiap.techchallenge.processor.dto.DocumentProcessingRequestedDTO;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.service.CreateInboxEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataExtractionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DataExtractionConsumer.class);

    private final CreateInboxEventService createInboxEventService;

    @Inject
    public DataExtractionConsumer(CreateInboxEventService createInboxEventService) {
        this.createInboxEventService = createInboxEventService;
    }

    @Incoming("data-extraction")
    public void dataExtraction(DocumentProcessingRequestedDTO message) {
        try {
            var inbox = new InboxDocumentProcessingRequest(message.eventId(), message.documentId(), message.fileUrl(), message.patientId());
            createInboxEventService.create(inbox);
            logger.info("action=documentProcessingRequestedDTO, eventId={}, documentId={}, filePath={}", message.eventId(), message.documentId(), message.fileUrl());
        } catch (Exception e) {
            logger.error("action=imagesUploadMessageReceivedError, reason={}", e.getMessage(), e);
        }
    }
}
