package br.com.fiap.techchallenge.processor.persistence.mapper.outbox;

import br.com.fiap.techchallenge.processor.domain.outbox.OutboxEvent;
import br.com.fiap.techchallenge.processor.persistence.entity.outbox.OutboxEventEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.DocumentoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {DocumentoMapper.class})
public interface OutboxEventMapper {
    OutboxEventEntity toEntity(OutboxEvent domain);
    OutboxEvent toDomain(OutboxEventEntity entity);
}
