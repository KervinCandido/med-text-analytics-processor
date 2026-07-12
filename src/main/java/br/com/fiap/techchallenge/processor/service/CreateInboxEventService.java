package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.dto.InboxEventDTO;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEvent;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxEventStatus;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;

@ApplicationScoped
public class CreateInboxEventService {

    private static final ZoneId SAO_PAULO_ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final Logger logger = LoggerFactory.getLogger(CreateInboxEventService.class);

    public void create(InboxEventDTO inboxEventDTO) {
        InboxEvent inboxEvent = new InboxEvent();
        inboxEvent.setInboxId(inboxEventDTO.getId());
        inboxEvent.setFilePaths(inboxEventDTO.getFilePaths());
        inboxEvent.setUserId(inboxEventDTO.getUserId());
        inboxEvent.setTotalFiles(inboxEventDTO.getTotalFiles());
        inboxEvent.setStatus(InboxEventStatus.PENDING);
        inboxEvent.setCreatedAt(LocalDateTime.now(SAO_PAULO_ZONE_ID));
        inboxEvent.persist();
        logger.info("action=saveInboxEventSuccess, inboxEvent={}", inboxEventDTO);
    }
}
