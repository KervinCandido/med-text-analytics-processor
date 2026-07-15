package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.Eritrograma;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.EritrogramaEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface EritrogramaMapper {
    EritrogramaEntity toEntity(Eritrograma domain);
    Eritrograma toDomain(EritrogramaEntity entity);
}
