package br.com.fiap.techchallenge.processor.domain;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public abstract class Document extends PanacheMongoEntity {

    private String patientId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
    protected abstract Optional<LocalDateTime> resolveDocumentDate();

    public void applyDocumentDateWithFallback(LocalDateTime dateTime) {
        this.documentDate = resolveDocumentDate().orElse(dateTime);
    }
}
