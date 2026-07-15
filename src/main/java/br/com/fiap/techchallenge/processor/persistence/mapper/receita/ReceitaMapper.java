package br.com.fiap.techchallenge.processor.persistence.mapper.receita;

import br.com.fiap.techchallenge.processor.persistence.entity.receita.ReceitaEntity;
import br.com.fiap.techchallenge.processor.domain.receita.Receita;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ReceitaMapper {
    ReceitaEntity toEntity(Receita domain);
    Receita toDomain(ReceitaEntity entity);
}
