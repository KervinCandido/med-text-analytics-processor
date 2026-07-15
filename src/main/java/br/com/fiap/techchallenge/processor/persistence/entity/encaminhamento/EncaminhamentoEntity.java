package br.com.fiap.techchallenge.processor.persistence.entity.encaminhamento;

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
public class EncaminhamentoEntity extends DocumentoEntity {
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

}
