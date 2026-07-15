package br.com.fiap.techchallenge.processor.persistence.mapper.relatorio;

import br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio;
import br.com.fiap.techchallenge.processor.persistence.entity.relatorio.RelatorioEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {ObjectIdMapper.class})
public interface RelatorioMapper {
    RelatorioEntity toEntity(Relatorio domain);
    Relatorio toDomain(RelatorioEntity entity);
}
