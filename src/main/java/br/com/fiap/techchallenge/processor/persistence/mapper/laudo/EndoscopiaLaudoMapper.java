package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.EndoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.domain.laudo.EndoscopiaLaudo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface EndoscopiaLaudoMapper {
    EndoscopiaLaudoEntity toEntity(EndoscopiaLaudo domain);
    EndoscopiaLaudo toDomain(EndoscopiaLaudoEntity entity);
}
