package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.T4LivreExame;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.T4LivreExameEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface T4LivreExameMapper {
    T4LivreExameEntity toEntity(T4LivreExame domain);
    T4LivreExame toDomain(T4LivreExameEntity entity);
}
