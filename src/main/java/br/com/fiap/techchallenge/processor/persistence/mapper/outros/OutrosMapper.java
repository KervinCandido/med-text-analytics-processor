package br.com.fiap.techchallenge.processor.persistence.mapper.outros;

import br.com.fiap.techchallenge.processor.persistence.entity.outros.OutrosEntity;
import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface OutrosMapper {
    OutrosEntity toEntity(Outros domain);
    Outros toDomain(OutrosEntity entity);
}
