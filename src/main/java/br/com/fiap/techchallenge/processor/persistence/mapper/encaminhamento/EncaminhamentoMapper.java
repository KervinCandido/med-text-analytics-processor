package br.com.fiap.techchallenge.processor.persistence.mapper.encaminhamento;

import br.com.fiap.techchallenge.processor.persistence.entity.encaminhamento.EncaminhamentoEntity;
import br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface EncaminhamentoMapper {
    EncaminhamentoEntity toEntity(Encaminhamento domain);
    Encaminhamento toDomain(EncaminhamentoEntity entity);
}
