package br.com.fiap.techchallenge.processor.domain.outbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OutboxDocumentResponse {

    private String outboxId;
    private ProcessingStatus status;
    private UUID eventId;
    private UUID documentId;
    private UUID patientId;
    private LocalDateTime createdAt;
    private List<String> documents;

    public OutboxDocumentResponse() {
        this.status = ProcessingStatus.PENDING;
        this.documents = new ArrayList<>();
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public void addDocumentId(String documentoId) {
        this.documents.add(documentoId);
    }

    public void failed() {
        if (ProcessingStatus.PROCESSING.equals(this.status)) {
            this.status = ProcessingStatus.FAILED;
        }
    }

    public void processing() {
        if (ProcessingStatus.PENDING.equals(this.status)) {
            this.status = ProcessingStatus.PROCESSING;
        }
    }

    public void processed() {
        if (ProcessingStatus.PROCESSING.equals(this.status)) {
            this.status = ProcessingStatus.PROCESSED;
        }
    }
}
