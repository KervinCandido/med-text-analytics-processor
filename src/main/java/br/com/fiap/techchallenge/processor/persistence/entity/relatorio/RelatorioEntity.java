package br.com.fiap.techchallenge.processor.persistence.entity.relatorio;

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
public class RelatorioEntity extends DocumentoEntity {
    private String historicoClinico;     // Anamnese, tempo de evolução, tratamentos prévios
    private String diagnostico;          // Descrição da patologia/lesão
    private String cid;                   // Código CID
    private String estadoAtual;          // ExameEntity físico, exames complementares, restrições físicas/cognitivas
    private String condutaMedica;        // Terapêutica atual, medicamentos, dosagens
    private String prognostico;          // Estimativa de evolução ou recuperação
    private String finalidade;           // Objetivo do relatório (perícia, escola, viagem)
    private LocalDateTime dataRelatorio; // Data da emissão do relatório
    private List<String> observacoes;
    private List<String> notas;
    private String descricaoGeral;
}
