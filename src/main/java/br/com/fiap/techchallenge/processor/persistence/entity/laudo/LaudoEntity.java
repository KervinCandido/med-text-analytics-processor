package br.com.fiap.techchallenge.processor.persistence.entity.laudo;

import br.com.fiap.techchallenge.processor.persistence.entity.DocumentoEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.time.LocalDateTime;
import java.util.List;

@BsonDiscriminator
@MongoEntity(collection = "documentos")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class LaudoEntity extends DocumentoEntity {
    private String laudoTipo; // e.g. UST_ABDOME_TOTAL, US_OBSTETRICO_ENDOVAGINAL, RESSONANCIA_MAGNETICA, TOMOGRAFIA_COMPUTADORIZADA, MAMOGRAFIA, ENDOSCOPIA_DIGESTIVA_ALTA, COLONOSCOPIA, etc.
    private String areaCorpo; // Área do corpo analisada (ex: Crânio, Ombro, Joelho, Coluna, Mamas, Aparelho Digestivo)
    private String tecnica;   // Técnica/Sequências utilizadas ou Dados Técnicos e Medicamentosos/Sedação
    private String descricaoAnatomica;
    private String achadosNormais;
    private String achadosPatologicos;
    private String impressaoDiagnostica;
    private LocalDateTime dataLaudo;
    private List<String> observacoes;
    private List<String> notas;
    private String descricaoGeral;

}
