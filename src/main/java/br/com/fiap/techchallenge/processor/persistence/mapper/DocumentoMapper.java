package br.com.fiap.techchallenge.processor.persistence.mapper;

import br.com.fiap.techchallenge.processor.domain.Documento;
import br.com.fiap.techchallenge.processor.domain.encaminhamento.Encaminhamento;
import br.com.fiap.techchallenge.processor.domain.exame.Exame;
import br.com.fiap.techchallenge.processor.domain.laudo.Laudo;
import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import br.com.fiap.techchallenge.processor.domain.receita.Receita;
import br.com.fiap.techchallenge.processor.domain.registro_atendimento.RegistroAtendimento;
import br.com.fiap.techchallenge.processor.domain.relatorio.Relatorio;
import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.encaminhamento.EncaminhamentoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.exame.ExameEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.laudo.LaudoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.outros.OutrosEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.receita.ReceitaEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.registro_atendimento.RegistroAtendimentoEntity;
import br.com.fiap.techchallenge.processor.persistence.entity.relatorio.RelatorioEntity;
import br.com.fiap.techchallenge.processor.persistence.mapper.encaminhamento.EncaminhamentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.exame.ExameMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.laudo.LaudoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.outros.OutrosMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.receita.ReceitaMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.registro_atendimento.RegistroAtendimentoMapper;
import br.com.fiap.techchallenge.processor.persistence.mapper.relatorio.RelatorioMapper;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    componentModel = "cdi",
    uses = {
        ObjectIdMapper.class,
        EncaminhamentoMapper.class,
        ExameMapper.class,
        LaudoMapper.class,
        OutrosMapper.class,
        ReceitaMapper.class,
        RegistroAtendimentoMapper.class,
        RelatorioMapper.class
    },
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public interface DocumentoMapper {

    @SubclassMapping(source = Encaminhamento.class, target = EncaminhamentoEntity.class)
    @SubclassMapping(source = Exame.class, target = ExameEntity.class)
    @SubclassMapping(source = Laudo.class, target = LaudoEntity.class)
    @SubclassMapping(source = Outros.class, target = OutrosEntity.class)
    @SubclassMapping(source = Receita.class, target = ReceitaEntity.class)
    @SubclassMapping(source = RegistroAtendimento.class, target = RegistroAtendimentoEntity.class)
    @SubclassMapping(source = Relatorio.class, target = RelatorioEntity.class)
    DocumentoEntity toEntity(Documento domain);

    @SubclassMapping(source = EncaminhamentoEntity.class, target = Encaminhamento.class)
    @SubclassMapping(source = ExameEntity.class, target = Exame.class)
    @SubclassMapping(source = LaudoEntity.class, target = Laudo.class)
    @SubclassMapping(source = OutrosEntity.class, target = Outros.class)
    @SubclassMapping(source = ReceitaEntity.class, target = Receita.class)
    @SubclassMapping(source = RegistroAtendimentoEntity.class, target = RegistroAtendimento.class)
    @SubclassMapping(source = RelatorioEntity.class, target = Relatorio.class)
    Documento toDomain(DocumentoEntity entity);

}
