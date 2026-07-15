package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.HemogramaExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.HemogramaExame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HemogramaExameMapper {
    HemogramaExameEntity toEntity(HemogramaExame domain);
    HemogramaExame toDomain(HemogramaExameEntity entity);
}
