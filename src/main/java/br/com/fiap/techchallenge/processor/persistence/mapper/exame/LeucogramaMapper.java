package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.LeucogramaEntity;
import br.com.fiap.techchallenge.processor.domain.exame.Leucograma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LeucogramaMapper {
    LeucogramaEntity toEntity(Leucograma domain);
    Leucograma toDomain(LeucogramaEntity entity);
}
