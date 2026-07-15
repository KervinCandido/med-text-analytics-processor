package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.MamografiaLaudo;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.MamografiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface MamografiaLaudoMapper {
    MamografiaLaudoEntity toEntity(MamografiaLaudo domain);
    MamografiaLaudo toDomain(MamografiaLaudoEntity entity);
}
