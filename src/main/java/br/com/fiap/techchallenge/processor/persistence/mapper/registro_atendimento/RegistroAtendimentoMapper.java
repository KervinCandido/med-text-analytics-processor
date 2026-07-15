package br.com.fiap.techchallenge.processor.persistence.mapper.registro_atendimento;

import br.com.fiap.techchallenge.processor.persistence.entity.registro_atendimento.RegistroAtendimentoEntity;
import br.com.fiap.techchallenge.processor.domain.registro_atendimento.RegistroAtendimento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface RegistroAtendimentoMapper {
    RegistroAtendimentoEntity toEntity(RegistroAtendimento domain);
    RegistroAtendimento toDomain(RegistroAtendimentoEntity entity);
}
