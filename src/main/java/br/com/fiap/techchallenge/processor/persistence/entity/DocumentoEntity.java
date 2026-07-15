package br.com.fiap.techchallenge.processor.persistence.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import br.com.fiap.techchallenge.processor.domain.DocumentType;

@Getter
@Setter
public abstract class DocumentEntity extends PanacheMongoEntity {

    private String patientId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
    protected abstract Optional<LocalDateTime> resolveDocumentDate();

    public void applyDocumentDateWithFallback(LocalDateTime dateTime) {
        this.documentDate = resolveDocumentDate().orElse(dateTime);
    }
}
