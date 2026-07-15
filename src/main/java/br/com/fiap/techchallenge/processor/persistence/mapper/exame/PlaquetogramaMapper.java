package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.Plaquetograma;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.PlaquetogramaEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface PlaquetogramaMapper {
    PlaquetogramaEntity toEntity(Plaquetograma domain);
    Plaquetograma toDomain(PlaquetogramaEntity entity);
}
