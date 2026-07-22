package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@ApplicationScoped
public class CreateInboxEventService {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    CreateInboxEventService.class
            );

    private final InboxDocumentProcessingRequestMapper
            inboxDocumentProcessingRequestMapper;

    private final InboxDocumentProcessingRequestRepository
            inboxRepository;

    @Inject
    public CreateInboxEventService(
            InboxDocumentProcessingRequestMapper
                    inboxDocumentProcessingRequestMapper,
            InboxDocumentProcessingRequestRepository
                    inboxRepository
    ) {
        this.inboxDocumentProcessingRequestMapper =
                inboxDocumentProcessingRequestMapper;

        this.inboxRepository =
                inboxRepository;
    }

    public void create(
            InboxDocumentProcessingRequest inbox
    ) {
        var entity =
                inboxDocumentProcessingRequestMapper
                        .toEntity(inbox);

        try {
            inboxRepository.persist(entity);

            logger.info(
                    "action=saveInboxEventSuccess, "
                            + "eventId={}, documentId={}",
                    inbox.getEventId(),
                    inbox.getDocumentId()
            );
        } catch (MongoWriteException exception) {
            if (!isDuplicateKey(exception)) {
                throw exception;
            }

            handleDuplicate(
                    inbox,
                    exception
            );
        }
    }

    private void handleDuplicate(
            InboxDocumentProcessingRequest incoming,
            MongoWriteException duplicateException
    ) {
        InboxDocumentProcessingRequestEntity existing =
                inboxRepository
                        .findByEventId(
                                incoming.getEventId()
                        )
                        .orElseThrow(
                                () -> duplicateException
                        );

        if (!representsSameRequest(
                existing,
                incoming
        )) {
            throw new IllegalStateException(
                    "Conflicting inbox event for eventId="
                            + incoming.getEventId(),
                    duplicateException
            );
        }

        logger.info(
                "action=ignoreDuplicateInboxEvent, "
                        + "eventId={}, documentId={}",
                incoming.getEventId(),
                incoming.getDocumentId()
        );
    }

    private static boolean isDuplicateKey(
            MongoWriteException exception
    ) {
        return ErrorCategory.DUPLICATE_KEY.equals(
                exception
                        .getError()
                        .getCategory()
        );
    }

    private static boolean representsSameRequest(
            InboxDocumentProcessingRequestEntity existing,
            InboxDocumentProcessingRequest incoming
    ) {
        return Objects.equals(
                existing.getEventId(),
                incoming.getEventId()
        ) && Objects.equals(
                existing.getDocumentId(),
                incoming.getDocumentId()
        ) && Objects.equals(
                existing.getPatientId(),
                incoming.getPatientId()
        ) && Objects.equals(
                existing.getFilePath(),
                incoming.getFilePath()
        ) && Objects.equals(
                existing.getContentType(),
                incoming.getContentType()
        );
    }
}
