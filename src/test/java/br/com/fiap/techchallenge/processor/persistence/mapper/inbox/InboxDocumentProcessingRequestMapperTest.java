package br.com.fiap.techchallenge.processor.persistence.mapper.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InboxDocumentProcessingRequestMapperTest {

    private static final String FILE_PATH =
            "/documents/exame.png";

    private static final String CONTENT_TYPE =
            "image/png";

    private final InboxDocumentProcessingRequestMapper mapper =
            createMapper();

    @Test
    void shouldPreservePersistedStateWhenRehydratingInbox() {
        ObjectId id = new ObjectId();
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 20, 19, 0);

        LocalDateTime processedAt =
                LocalDateTime.of(2026, 7, 20, 19, 5);

        InboxDocumentProcessingRequestEntity entity =
                new InboxDocumentProcessingRequestEntity();

        entity.setId(id);
        entity.setEventId(eventId);
        entity.setDocumentId(documentId);
        entity.setPatientId(patientId);
        entity.setFilePath(FILE_PATH);
        entity.setContentType(CONTENT_TYPE);
        entity.setStatus(ProcessingStatus.FAILED);
        entity.setCreatedAt(createdAt);
        entity.setProcessedAt(processedAt);
        entity.setRetryCount((short) 2);

        InboxDocumentProcessingRequest domain =
                mapper.toDomain(entity);

        assertAll(
                () -> assertEquals(
                        id.toHexString(),
                        domain.getId()
                ),
                () -> assertEquals(
                        eventId,
                        domain.getEventId()
                ),
                () -> assertEquals(
                        documentId,
                        domain.getDocumentId()
                ),
                () -> assertEquals(
                        patientId,
                        domain.getPatientId()
                ),
                () -> assertEquals(
                        FILE_PATH,
                        domain.getFilePath()
                ),
                () -> assertEquals(
                        CONTENT_TYPE,
                        domain.getContentType()
                ),
                () -> assertEquals(
                        ProcessingStatus.FAILED,
                        domain.getStatus()
                ),
                () -> assertEquals(
                        createdAt,
                        domain.getCreatedAt()
                ),
                () -> assertEquals(
                        processedAt,
                        domain.getProcessedAt()
                ),
                () -> assertEquals(
                        (short) 2,
                        domain.getRetryCount()
                )
        );
    }

    @Test
    void shouldPreserveDomainStateWhenMappingToEntity() {
        ObjectId id = new ObjectId();
        UUID eventId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 7, 20, 20, 0);

        LocalDateTime processedAt =
                LocalDateTime.of(2026, 7, 20, 20, 5);

        InboxDocumentProcessingRequest domain =
                InboxDocumentProcessingRequest.rehydrate(
                        id.toHexString(),
                        eventId,
                        documentId,
                        FILE_PATH,
                        CONTENT_TYPE,
                        patientId,
                        ProcessingStatus.FAILED,
                        createdAt,
                        processedAt,
                        (short) 2
                );

        InboxDocumentProcessingRequestEntity entity =
                mapper.toEntity(domain);

        assertAll(
                () -> assertEquals(
                        id,
                        entity.getId()
                ),
                () -> assertEquals(
                        eventId,
                        entity.getEventId()
                ),
                () -> assertEquals(
                        documentId,
                        entity.getDocumentId()
                ),
                () -> assertEquals(
                        patientId,
                        entity.getPatientId()
                ),
                () -> assertEquals(
                        FILE_PATH,
                        entity.getFilePath()
                ),
                () -> assertEquals(
                        CONTENT_TYPE,
                        entity.getContentType()
                ),
                () -> assertEquals(
                        ProcessingStatus.FAILED,
                        entity.getStatus()
                ),
                () -> assertEquals(
                        createdAt,
                        entity.getCreatedAt()
                ),
                () -> assertEquals(
                        processedAt,
                        entity.getProcessedAt()
                ),
                () -> assertEquals(
                        (short) 2,
                        entity.getRetryCount()
                )
        );
    }

    private static InboxDocumentProcessingRequestMapper
    createMapper() {
        InboxDocumentProcessingRequestMapper mapper =
                Mappers.getMapper(
                        InboxDocumentProcessingRequestMapper.class
                );

        try {
            Field objectIdMapperField =
                    mapper.getClass().getDeclaredField(
                            "objectIdMapper"
                    );

            objectIdMapperField.setAccessible(true);

            objectIdMapperField.set(
                    mapper,
                    new ObjectIdMapper()
            );

            return mapper;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Could not configure generated inbox mapper",
                    exception
            );
        }
    }
}
