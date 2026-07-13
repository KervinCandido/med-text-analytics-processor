package br.com.fiap.techchallenge.processor.domain.laudo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import com.fasterxml.jackson.annotation.JsonInclude;
import br.com.fiap.techchallenge.processor.domain.Document;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "laudos")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Laudo extends Document {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
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

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataLaudo);
    }
}
