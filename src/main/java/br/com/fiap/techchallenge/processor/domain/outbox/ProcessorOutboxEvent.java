package br.com.fiap.techchallenge.processor.domain.outbox;

import java.time.LocalDateTime;

public class ProcessorOutboxEvent {
    public String jobId;
    public String payload; // JSON representation of JobStatusUpdateDTO
    public String status; // PENDING, PROCESSED, FAILED
    public LocalDateTime createdAt;
}
