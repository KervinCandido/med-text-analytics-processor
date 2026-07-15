package br.com.fiap.techchallenge.processor.domain;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public abstract class Document {

    private ObjectId id;
    private String patientId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
    
    protected abstract Optional<LocalDateTime> resolveDocumentDate();

    public void applyDocumentDateWithFallback(LocalDateTime dateTime) {
        this.documentDate = resolveDocumentDate().orElse(dateTime);
    }
}
