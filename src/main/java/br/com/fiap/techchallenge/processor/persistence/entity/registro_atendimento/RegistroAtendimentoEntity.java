package br.com.fiap.techchallenge.processor.persistence.entity.registro_atendimento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.Optional;

@MongoEntity(collection = "registros_atendimento")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAtendimentoEntity extends DocumentEntity {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private String paciente;
    private String medico;
    private LocalDateTime dataAtendimento;
    private String motivoAtendimento;
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataAtendimento);
    }
}
