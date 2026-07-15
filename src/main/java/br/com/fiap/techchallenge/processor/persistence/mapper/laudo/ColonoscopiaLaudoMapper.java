package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.ColonoscopiaLaudo;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.ColonoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface ColonoscopiaLaudoMapper {
    ColonoscopiaLaudoEntity toEntity(ColonoscopiaLaudo domain);
    ColonoscopiaLaudo toDomain(ColonoscopiaLaudoEntity entity);
}
