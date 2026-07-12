package br.com.fiap.techchallenge.processor.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public abstract class Document extends PanacheMongoEntity {
    private String userId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
    protected abstract Optional<LocalDateTime> documentDate();
    public void applyDocumentDateWithFallback(LocalDateTime dateTime) {
        this.documentDate = documentDate().orElse(dateTime);
    }
}
