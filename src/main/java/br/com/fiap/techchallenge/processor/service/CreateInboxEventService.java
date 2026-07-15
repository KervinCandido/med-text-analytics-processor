package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.dto.InboxEventDTO;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxEventEntity;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CreateInboxEventService {

    private static final Logger logger = LoggerFactory.getLogger(CreateInboxEventService.class);

    public void create(InboxEventDTO inboxEventDTO) {
        InboxEventEntity inboxEvent = new InboxEventEntity();
        inboxEvent.setEventId(String.valueOf(inboxEventDTO.eventId()));
        inboxEvent.setDocumentId(String.valueOf(inboxEventDTO.documentId()));
        inboxEvent.setPatientId(String.valueOf(inboxEventDTO.patientId()));
        inboxEvent.setFilePath(inboxEventDTO.filePath());
        inboxEvent.persist();
        logger.info("action=saveInboxEventSuccess, inboxEvent={}", inboxEventDTO);
    }
}
