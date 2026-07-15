package br.com.fiap.techchallenge.processor.persistence.mapper.exame;

import br.com.fiap.techchallenge.processor.domain.exame.*;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.*;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(
    componentModel = "cdi",
    uses = {
        ObjectIdMapper.class,
        BetaHcgExameMapper.class,
        GlicemiaJejumExameMapper.class,
        HemoglobinaGlicadaExameMapper.class,
        HemogramaExameMapper.class,
        LipidogramaExameMapper.class,
        T4LivreExameMapper.class,
        TshExame.class
    }
)
public interface ExameMapper {

    @SubclassMapping(source = BetaHcgExame.class, target = BetaHcgExameEntity.class)
    @SubclassMapping(source = GlicemiaJejumExame.class, target = GlicemiaJejumExameEntity.class)
    @SubclassMapping(source = HemoglobinaGlicadaExame.class, target = HemoglobinaGlicadaExameEntity.class)
    @SubclassMapping(source = HemogramaExame.class, target = HemogramaExameEntity.class)
    @SubclassMapping(source = LipidogramaExame.class, target = LipidogramaExameEntity.class)
    @SubclassMapping(source = T4LivreExame.class, target = T4LivreExameEntity.class)
    @SubclassMapping(source = TshExame.class, target = TshExameEntity.class)
    ExameEntity toEntity(Exame domain);

    @SubclassMapping(source = BetaHcgExameEntity.class, target = BetaHcgExame.class)
    @SubclassMapping(source = GlicemiaJejumExameEntity.class, target = GlicemiaJejumExame.class)
    @SubclassMapping(source = HemoglobinaGlicadaExameEntity.class, target = HemoglobinaGlicadaExame.class)
    @SubclassMapping(source = HemogramaExameEntity.class, target = HemogramaExame.class)
    @SubclassMapping(source = LipidogramaExameEntity.class, target = LipidogramaExame.class)
    @SubclassMapping(source = T4LivreExameEntity.class, target = T4LivreExame.class)
    @SubclassMapping(source = TshExameEntity.class, target = TshExame.class)
    Exame toDomain(ExameEntity entity);
}
