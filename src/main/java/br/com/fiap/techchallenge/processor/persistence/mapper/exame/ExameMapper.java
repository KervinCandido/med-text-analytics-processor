package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.ExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.Exame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ExameMapper {
    ExameEntity toEntity(Exame domain);
    Exame toDomain(ExameEntity entity);
}
