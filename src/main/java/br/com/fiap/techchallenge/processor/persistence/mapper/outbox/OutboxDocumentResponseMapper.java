package br.com.fiap.techchallenge.processor.persistence.mapper.outbox;

import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxDocumentResponseEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {DocumentoMapper.class, ObjectIdMapper.class})
public interface OutboxDocumentResponseMapper {
    OutboxDocumentResponseEntity toEntity(OutboxDocumentResponse domain);
    OutboxDocumentResponse toDomain(OutboxDocumentResponseEntity entity);
}
