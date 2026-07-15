package br.com.fiap.techchallenge.processor.domain.outros;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import br.com.fiap.techchallenge.processor.domain.Document;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class Outros extends Document {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(uploadedAt);
    }
}
