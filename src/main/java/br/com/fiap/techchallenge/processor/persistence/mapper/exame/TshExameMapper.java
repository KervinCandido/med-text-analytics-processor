package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.TshExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.TshExame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface TshExameMapper {
    TshExameEntity toEntity(TshExame domain);
    TshExame toDomain(TshExameEntity entity);
}
