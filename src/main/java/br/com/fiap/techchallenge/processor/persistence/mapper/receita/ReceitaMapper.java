package br.com.fiap.techchallenge.processor.persistence.mapper.receita;

import br.com.fiap.techchallenge.processor.domain.receita.Receita;
import br.com.fiap.techchallenge.processor.persistence.entity.receita.ReceitaEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class, ReceitaItemMapper.class})
public interface ReceitaMapper {
    ReceitaEntity toEntity(Receita domain);
    Receita toDomain(ReceitaEntity entity);
}
