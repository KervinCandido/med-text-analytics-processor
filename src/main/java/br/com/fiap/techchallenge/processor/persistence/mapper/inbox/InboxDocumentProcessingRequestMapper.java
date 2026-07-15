package br.com.fiap.techchallenge.processor.persistence.mapper.inbox;

import br.com.fiap.techchallenge.processor.domain.inbox.InboxDocumentProcessingRequest;
import br.com.fiap.techchallenge.processor.persistence.entity.inbox.InboxDocumentProcessingRequestEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = ObjectIdMapper.class)
public interface InboxEventMapper {
    InboxDocumentProcessingRequestEntity toEntity(InboxDocumentProcessingRequest domain);
    InboxDocumentProcessingRequest toDomain(InboxDocumentProcessingRequestEntity entity);
}
