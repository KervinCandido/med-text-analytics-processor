package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.Leucograma;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.LeucogramaEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface LeucogramaMapper {
    LeucogramaEntity toEntity(Leucograma domain);
    Leucograma toDomain(LeucogramaEntity entity);
}
