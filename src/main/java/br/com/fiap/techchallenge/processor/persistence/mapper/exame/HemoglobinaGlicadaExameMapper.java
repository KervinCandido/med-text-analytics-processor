package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.persistence.entity.exame.HemoglobinaGlicadaExameEntity;
import br.com.fiap.techchallenge.processor.domain.exame.HemoglobinaGlicadaExame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface HemoglobinaGlicadaExameMapper {
    HemoglobinaGlicadaExameEntity toEntity(HemoglobinaGlicadaExame domain);
    HemoglobinaGlicadaExame toDomain(HemoglobinaGlicadaExameEntity entity);
}
