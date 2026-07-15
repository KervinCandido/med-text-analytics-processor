package br.com.fiap.techchallenge.processor.persistence.entity.outros;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.Optional;

@MongoEntity(collection = "outros")
@Getter
@Setter
@NoArgsConstructor
public class OutrosEntity extends DocumentEntity {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(uploadedAt);
    }
}
