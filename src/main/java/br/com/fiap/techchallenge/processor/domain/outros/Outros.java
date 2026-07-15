package br.com.fiap.techchallenge.processor.domain.outros;

import br.com.fiap.techchallenge.processor.domain.Documento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class Outros extends Documento {
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.empty();
    }
}
