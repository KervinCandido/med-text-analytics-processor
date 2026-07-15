package br.com.fiap.techchallenge.processor.domain.encaminhamento;

import br.com.fiap.techchallenge.processor.domain.Documento;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class Encaminhamento extends Documento {
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
