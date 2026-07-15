package br.com.fiap.techchallenge.processor.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public abstract class Documento {

    private String id;
    private UUID patientId;
    private DocumentType documentType;
    private LocalDateTime documentDate;
    
    protected abstract Optional<LocalDateTime> resolveDocumentDate();

    public void applyDocumentDateWithFallback(LocalDateTime dateTime) {
        this.documentDate = resolveDocumentDate().orElse(dateTime);
    }
}
