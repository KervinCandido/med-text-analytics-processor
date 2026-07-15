package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.ColonoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.domain.laudo.ColonoscopiaLaudo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ColonoscopiaLaudoMapper {
    ColonoscopiaLaudoEntity toEntity(ColonoscopiaLaudo domain);
    ColonoscopiaLaudo toDomain(ColonoscopiaLaudoEntity entity);
}
