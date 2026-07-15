package br.com.fiap.techchallenge.processor.persistence.mapper.laudo;

import br.com.fiap.techchallenge.processor.domain.laudo.ColonoscopiaLaudo;
import br.com.fiap.techchallenge.processor.domain.laudo.EndoscopiaLaudo;
import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.domain.laudo.MamografiaLaudo;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.ColonoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.EndoscopiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.LaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.MamografiaLaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.ObjectIdMapper;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(
    componentModel = "cdi",
    uses = {
        ObjectIdMapper.class,
        ColonoscopiaLaudoMapper.class,
        EndoscopiaLaudoMapper.class,
        MamografiaLaudoMapper.class
    }
)
public interface LaudoMapper {

    @SubclassMapping(source = ColonoscopiaLaudo.class, target = ColonoscopiaLaudoEntity.class)
    @SubclassMapping(source = EndoscopiaLaudo.class, target = EndoscopiaLaudoEntity.class)
    @SubclassMapping(source = MamografiaLaudo.class, target = MamografiaLaudoEntity.class)
    LaudoEntity toEntity(Laudo domain);

    @SubclassMapping(source = ColonoscopiaLaudoEntity.class, target = ColonoscopiaLaudo.class)
    @SubclassMapping(source = EndoscopiaLaudoEntity.class, target = EndoscopiaLaudo.class)
    @SubclassMapping(source = MamografiaLaudoEntity.class, target = MamografiaLaudo.class)
    Laudo toDomain(LaudoEntity entity);
}
