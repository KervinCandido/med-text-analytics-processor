package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.LaudoEntity;
import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface LaudoMapper {
    LaudoEntity toEntity(Laudo domain);
    Laudo toDomain(LaudoEntity entity);
}
