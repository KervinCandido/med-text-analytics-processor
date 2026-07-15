package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.HemogramaExame;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.HemogramaExameEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class, EritrogramaMapper.class, LeucogramaMapper.class})
public interface HemogramaExameMapper {
    HemogramaExameEntity toEntity(HemogramaExame domain);
    HemogramaExame toDomain(HemogramaExameEntity entity);
}
