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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDateTime documentDate) {
        this.documentDate = documentDate;
    }
}
