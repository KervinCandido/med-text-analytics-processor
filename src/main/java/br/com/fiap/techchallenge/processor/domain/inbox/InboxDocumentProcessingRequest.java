package br.com.fiap.techchallenge.processor.domain.inbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public final class InboxDocumentProcessingRequest {

    public static final int RETRY_LIMIT = 3;
    private final String id;
    private final UUID eventId;
    private final UUID documentId;
    private final String filePath;
    private final UUID patientId;
    private ProcessingStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Short retryCount;

    public InboxDocumentProcessingRequest(
            String id,
            UUID eventId,
            UUID documentId,
            String filePath,
            UUID patientId) {
        this.id = id;
        this.eventId = eventId;
        this.documentId = documentId;
        this.filePath = filePath;
        this.patientId = patientId;
        this.status = ProcessingStatus.PENDING;
        this.createdAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        this.retryCount = Short.valueOf("0");
    }

    public void processing() {
        if (ProcessingStatus.PENDING.equals(status) || ProcessingStatus.FAILED.equals(status)) {
            this.status = ProcessingStatus.PROCESSING;
        }
    }

    public void processed() {
        if (ProcessingStatus.PROCESSING.equals(status)) {
            this.status = ProcessingStatus.PROCESSED;
            this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
        }
    }

    public void failed() {
        if (RETRY_LIMIT < retryCount) {
            this.status = ProcessingStatus.ALL_RETRY_FAILED;
        } else if (ProcessingStatus.PROCESSING.equals(status)) {
            this.status = ProcessingStatus.FAILED;
            this.retryCount++;
        }
        this.processedAt = LocalDateTime.now(Constants.SAO_PAULO_ZONE_ID);
    }

    public void reprocessByRateLimit() {
        this.status = ProcessingStatus.PENDING;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InboxDocumentProcessingRequest) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.eventId, that.eventId) &&
                Objects.equals(this.documentId, that.documentId) &&
                Objects.equals(this.filePath, that.filePath) &&
                Objects.equals(this.patientId, that.patientId) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.processedAt, that.processedAt) &&
                Objects.equals(this.retryCount, that.retryCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, documentId, filePath, patientId, status, createdAt, processedAt, retryCount);
    }

}
