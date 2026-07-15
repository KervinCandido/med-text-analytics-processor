package br.com.fiap.techchallenge.processor.domain.relatorio;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import com.fasterxml.jackson.annotation.JsonInclude;
import br.com.fiap.techchallenge.processor.domain.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Relatorio extends Document {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private String historicoClinico;     // Anamnese, tempo de evolução, tratamentos prévios
    private String diagnostico;          // Descrição da patologia/lesão
    private String cid;                   // Código CID
    private String estadoAtual;          // Exame físico, exames complementares, restrições físicas/cognitivas
    private String condutaMedica;        // Terapêutica atual, medicamentos, dosagens
    private String prognostico;          // Estimativa de evolução ou recuperação
    private String finalidade;           // Objetivo do relatório (perícia, escola, viagem)
    private LocalDateTime dataRelatorio; // Data da emissão do relatório
    private List<String> observacoes;
    private List<String> notas;
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataRelatorio);
    }
}
