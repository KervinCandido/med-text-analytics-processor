package br.com.fiap.techchallenge.processor.persistence.mapper.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InboxDocumentProcessingRequestMapperTest {

    private final InboxDocumentProcessingRequestMapper mapper =
            Mappers.getMapper(
                    InboxDocumentProcessingRequestMapper.class
            );

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
        entity.setFilePath("/documents/exame.png");
        entity.setStatus(ProcessingStatus.FAILED);
        entity.setCreatedAt(createdAt);
        entity.setProcessedAt(processedAt);
        entity.setRetryCount((short) 2);

        var domain = mapper.toDomain(entity);

        assertEquals(id.toHexString(), domain.getId());
        assertEquals(eventId, domain.getEventId());
        assertEquals(documentId, domain.getDocumentId());
        assertEquals(patientId, domain.getPatientId());
        assertEquals("/documents/exame.png", domain.getFilePath());

        assertEquals(
                ProcessingStatus.FAILED,
                domain.getStatus()
        );

        assertEquals(createdAt, domain.getCreatedAt());
        assertEquals(processedAt, domain.getProcessedAt());
        assertEquals((short) 2, domain.getRetryCount());
    }
}
