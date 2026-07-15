package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.LipidogramaExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.LipidogramaExame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LipidogramaExameMapper {
    LipidogramaExameEntity toEntity(LipidogramaExame domain);
    LipidogramaExame toDomain(LipidogramaExameEntity entity);
}
