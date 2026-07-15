package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.PlaquetogramaEntity;
import br.com.fiap.techchallenge.processor.domain.exame.Plaquetograma;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface PlaquetogramaMapper {
    PlaquetogramaEntity toEntity(Plaquetograma domain);
    Plaquetograma toDomain(PlaquetogramaEntity entity);
}
