package br.com.fiap.techchallenge.processor.persistence.mapper.outros;

import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import br.com.fiap.techchallenge.processor.persistence.entity.outros.OutrosEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface OutrosMapper {
    OutrosEntity toEntity(Outros domain);
    Outros toDomain(OutrosEntity entity);
}
