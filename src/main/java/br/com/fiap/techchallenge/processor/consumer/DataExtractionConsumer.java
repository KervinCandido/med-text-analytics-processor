package br.com.fiap.techchallenge.processor.consumer;

import br.com.fiap.techchallenge.processor.dto.DocumentsUploadMessageDTO;
import br.com.fiap.techchallenge.processor.dto.InboxEventDTO;
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
    public void dataExtraction(DocumentsUploadMessageDTO message) {
        try {
            var inboxEventDTO = new InboxEventDTO(message.getDocumentsUploadId(), message.getFileUrls(), message.getPatientId(), message.getFileUrls().size());
            createInboxEventService.create(inboxEventDTO);
            logger.info("action=imagesUploadMessageReceivedSuccess, jobId={}, filePaths={}", message.getDocumentsUploadId(), message.getFileUrls());
        } catch (Exception e) {
            logger.error("action=imagesUploadMessageReceivedError, reason={}", e.getMessage(), e);
        }
    }
}
