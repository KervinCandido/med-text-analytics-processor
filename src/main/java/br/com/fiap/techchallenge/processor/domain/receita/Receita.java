package br.com.fiap.techchallenge.processor.domain.receita;

import br.com.fiap.techchallenge.processor.domain.Documento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class Receita extends Documento {
    private List<ReceitaItem> itens;
    private List<String> observacoes;
    private List<String> notas;
    private LocalDateTime dataReceita;
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataReceita);
    }
}
