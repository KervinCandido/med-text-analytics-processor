package br.com.fiap.techchallenge.processor.persistence.mapper.inbox;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "cdi",
        uses = ObjectIdMapper.class
)
public interface InboxDocumentProcessingRequestMapper {

    @Mapping(target = "id", source = "id")
    InboxDocumentProcessingRequestEntity toEntity(
            InboxDocumentProcessingRequest domain
    );

    default InboxDocumentProcessingRequest toDomain(
            InboxDocumentProcessingRequestEntity entity
    ) {
        if (entity == null) {
            return null;
        }

        String id = entity.getId() == null
                ? null
                : entity.getId().toHexString();

        return InboxDocumentProcessingRequest.rehydrate(
                id,
                entity.getEventId(),
                entity.getDocumentId(),
                entity.getFilePath(),
                entity.getContentType(),
                entity.getPatientId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getProcessedAt(),
                entity.getRetryCount()
        );
    }
}
