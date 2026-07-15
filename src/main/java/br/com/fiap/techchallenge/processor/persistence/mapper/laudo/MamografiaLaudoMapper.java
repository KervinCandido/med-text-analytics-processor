package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.laudo.MamografiaLaudoEntity;
import br.com.fiap.techchallenge.processor.domain.laudo.MamografiaLaudo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface MamografiaLaudoMapper {
    MamografiaLaudoEntity toEntity(MamografiaLaudo domain);
    MamografiaLaudo toDomain(MamografiaLaudoEntity entity);
}
