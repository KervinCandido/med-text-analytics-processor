package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.InboxDocumentProcessingRequestRepository;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.inbox.InboxDocumentProcessingRequestMapper;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.WriteError;
import org.bson.BsonDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateInboxEventServiceTest {

    private static final UUID EVENT_ID =
            UUID.fromString(
                    "a55c53fa-f3c4-4552-a98f-7d88d16f40e2"
            );

    private static final UUID DOCUMENT_ID =
            UUID.fromString(
                    "60ebc948-f701-462f-bffd-9167eb77b193"
            );

    private static final UUID PATIENT_ID =
            UUID.fromString(
                    "240e349a-e621-44d3-b569-870bf5825939"
            );

    private static final String FILE_URL =
            "https://patient-document-service:8443/documents/"
                    + DOCUMENT_ID
                    + "/file";

    private static final String CONTENT_TYPE =
            "image/png";

    @Mock
    private InboxDocumentProcessingRequestMapper mapper;

    @Mock
    private InboxDocumentProcessingRequestRepository repository;

    private CreateInboxEventService service;

    @BeforeEach
    void setUp() {
        service =
                new CreateInboxEventService(
                        mapper,
                        repository
                );
    }

    @Test
    void shouldPersistNewInboxEvent() {
        InboxDocumentProcessingRequest inbox =
                inboxRequest();

        InboxDocumentProcessingRequestEntity entity =
                matchingEntity();

        when(mapper.toEntity(inbox))
                .thenReturn(entity);

        service.create(inbox);

        verify(repository).persist(entity);

        verify(repository, never())
                .findByEventId(EVENT_ID);
    }

    @Test
    void shouldIgnoreDuplicateWithSamePayload() {
        InboxDocumentProcessingRequest inbox =
                inboxRequest();

        InboxDocumentProcessingRequestEntity entity =
                matchingEntity();

        MongoWriteException duplicateException =
                mongoWriteException(11000);

        when(mapper.toEntity(inbox))
                .thenReturn(entity);

        doThrow(duplicateException)
                .when(repository)
                .persist(entity);

        when(repository.findByEventId(EVENT_ID))
                .thenReturn(
                        Optional.of(entity)
                );

        assertDoesNotThrow(
                () -> service.create(inbox)
        );

        verify(repository)
                .findByEventId(EVENT_ID);
    }

    @Test
    void shouldRejectDuplicateWithDifferentDocumentId() {
        InboxDocumentProcessingRequest inbox =
                inboxRequest();

        InboxDocumentProcessingRequestEntity mappedEntity =
                matchingEntity();

        InboxDocumentProcessingRequestEntity existingEntity =
                matchingEntity();

        existingEntity.setDocumentId(
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                )
        );

        MongoWriteException duplicateException =
                mongoWriteException(11000);

        when(mapper.toEntity(inbox))
                .thenReturn(mappedEntity);

        doThrow(duplicateException)
                .when(repository)
                .persist(mappedEntity);

        when(repository.findByEventId(EVENT_ID))
                .thenReturn(
                        Optional.of(existingEntity)
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.create(inbox)
                );

        assertEquals(
                "Conflicting inbox event for eventId=" + EVENT_ID,
                exception.getMessage()
        );

        assertSame(
                duplicateException,
                exception.getCause()
        );
    }

    @Test
    void shouldRejectDuplicateWithDifferentContentType() {
        InboxDocumentProcessingRequest inbox =
                inboxRequest();

        InboxDocumentProcessingRequestEntity mappedEntity =
                matchingEntity();

        InboxDocumentProcessingRequestEntity existingEntity =
                matchingEntity();

        existingEntity.setContentType("image/jpeg");

        MongoWriteException duplicateException =
                mongoWriteException(11000);

        when(mapper.toEntity(inbox))
                .thenReturn(mappedEntity);

        doThrow(duplicateException)
                .when(repository)
                .persist(mappedEntity);

        when(repository.findByEventId(EVENT_ID))
                .thenReturn(
                        Optional.of(existingEntity)
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.create(inbox)
                );

        assertEquals(
                "Conflicting inbox event for eventId=" + EVENT_ID,
                exception.getMessage()
        );

        assertSame(
                duplicateException,
                exception.getCause()
        );
    }

    @Test
    void shouldPropagateNonDuplicateWriteFailure() {
        InboxDocumentProcessingRequest inbox =
                inboxRequest();

        InboxDocumentProcessingRequestEntity entity =
                matchingEntity();

        MongoWriteException writeException =
                mongoWriteException(121);

        when(mapper.toEntity(inbox))
                .thenReturn(entity);

        doThrow(writeException)
                .when(repository)
                .persist(entity);

        MongoWriteException propagated =
                assertThrows(
                        MongoWriteException.class,
                        () -> service.create(inbox)
                );

        assertSame(
                writeException,
                propagated
        );

        verify(repository, never())
                .findByEventId(EVENT_ID);
    }

    private static InboxDocumentProcessingRequest
    inboxRequest() {
        return new InboxDocumentProcessingRequest(
                null,
                EVENT_ID,
                DOCUMENT_ID,
                FILE_URL,
                CONTENT_TYPE,
                PATIENT_ID
        );
    }

    private static InboxDocumentProcessingRequestEntity
    matchingEntity() {
        InboxDocumentProcessingRequestEntity entity =
                new InboxDocumentProcessingRequestEntity();

        entity.setEventId(EVENT_ID);
        entity.setDocumentId(DOCUMENT_ID);
        entity.setPatientId(PATIENT_ID);
        entity.setFilePath(FILE_URL);
        entity.setContentType(CONTENT_TYPE);

        return entity;
    }

    private static MongoWriteException
    mongoWriteException(int errorCode) {
        return new MongoWriteException(
                new WriteError(
                        errorCode,
                        "MongoDB write failure",
                        new BsonDocument()
                ),
                new ServerAddress(),
                java.util.Set.of()
        );
    }
}
