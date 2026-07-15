package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.BetaHcgExame;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.BetaHcgExameEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface BetaHcgExameMapper {
    BetaHcgExameEntity toEntity(BetaHcgExame domain);
    BetaHcgExame toDomain(BetaHcgExameEntity entity);
}
