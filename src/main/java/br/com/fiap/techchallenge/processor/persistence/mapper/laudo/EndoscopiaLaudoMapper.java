package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.EndoscopiaLaudo;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.EndoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface EndoscopiaLaudoMapper {
    EndoscopiaLaudoEntity toEntity(EndoscopiaLaudo domain);
    EndoscopiaLaudo toDomain(EndoscopiaLaudoEntity entity);
}
