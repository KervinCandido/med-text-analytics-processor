package br.com.fiap.techchallenge.processor.domain.registro_atendimento;

import br.com.fiap.techchallenge.processor.domain.Documento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class RegistroAtendimento extends Documento {
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
