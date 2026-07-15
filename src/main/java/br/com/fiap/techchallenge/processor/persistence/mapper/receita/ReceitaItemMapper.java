package br.com.fiap.techchallenge.processor.persistence.mapper.receita;

import br.com.fiap.techchallenge.processor.domain.receita.ReceitaItem;
import br.com.fiap.techchallenge.processor.persistence.entity.receita.ReceitaItemEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface ReceitaItemMapper {
    ReceitaItemEntity toEntity(ReceitaItem domain);
    ReceitaItem toDomain(ReceitaItemEntity entity);
}
