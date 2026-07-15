package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.GlicemiaJejumExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.GlicemiaJejumExame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface GlicemiaJejumExameMapper {
    GlicemiaJejumExameEntity toEntity(GlicemiaJejumExame domain);
    GlicemiaJejumExame toDomain(GlicemiaJejumExameEntity entity);
}
