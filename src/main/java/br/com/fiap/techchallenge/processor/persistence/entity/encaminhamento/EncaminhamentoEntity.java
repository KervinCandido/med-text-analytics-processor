package br.com.fiap.techchallenge.processor.persistence.entity.encaminhamento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import com.fasterxml.jackson.annotation.JsonInclude;
import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "encaminhamentos")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class EncaminhamentoEntity extends DocumentEntity {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    // Destino
    private String especialidadeDestino;  // Especialidade médica desejada
    private String instituicaoDestino;    // Hospital, ambulatório ou serviço de destino
    
    // Justificativa e Motivo
    private String motivo;               // Hipótese diagnóstica ou diagnóstico confirmado
    private String resumoCaso;           // Sintomas, tratamentos prévios, exames relevantes
    private String objetivo;             // O que se espera do especialista (ex: avaliação, conduta cirúrgica)
    
    // Prioridade
    private String prioridade;           // Classificação de risco (Emergência, Urgência, Prioritário, Eletivo/Rotina)
    
    // Encerramento
    private LocalDateTime dataEmissao;   // Data de emissão da guia
    
    private List<String> observacoes;
    private List<String> notas;
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataEmissao);
    }
}
