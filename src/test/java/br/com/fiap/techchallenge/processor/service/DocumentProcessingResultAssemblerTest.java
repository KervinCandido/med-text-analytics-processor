package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.domain.exame.HemogramaExame;
import br.com.fiap.techchallenge.processor.domain.receita.Receita;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingCompletedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingFailedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class DocumentProcessingResultAssemblerTest {

    private final DocumentProcessingResultAssembler assembler =
            new DocumentProcessingResultAssembler(
                    new ObjectMapper()
                            .findAndRegisterModules()
                            .disable(
                                    SerializationFeature
                                            .WRITE_DATES_AS_TIMESTAMPS
                            )
            );

    @Test
    void shouldAssembleDeterministicCompletedResult() {
        UUID patientId = UUID.randomUUID();

        HemogramaExame hemograma =
                new HemogramaExame();

        hemograma.setId("hemograma-result");
        hemograma.setPatientId(patientId);
        hemograma.setDocumentType(
                DocumentType.EXAME_HEMOGRAMA
        );
        hemograma.setDocumentDate(
                LocalDateTime.of(
                        2026,
                        6,
                        10,
                        9,
                        30
                )
        );
        hemograma.setExameTipo("HEMOGRAMA");
        hemograma.setMaterial("Sangue total");
        hemograma.setDescricaoGeral(
                "Hemograma completo."
        );

        Receita receita = new Receita();

        receita.setId("receita-result");
        receita.setPatientId(patientId);
        receita.setDocumentType(
                DocumentType.RECEITA
        );
        receita.setDocumentDate(
                LocalDateTime.of(
                        2026,
                        6,
                        12,
                        14,
                        0
                )
        );
        receita.setDescricaoGeral(
                "Receita médica."
        );

        DocumentProcessingCompletedDTO result =
                assembler.completed(
                        completedOutbox(patientId),
                        List.of(
                                receita,
                                hemograma
                        )
                );

        assertEquals(
                "EXAME_LABORATORIAL",
                result.primaryDocumentType()
        );

        assertEquals(
                "Documento processado contendo: "
                        + "exame hemograma, receita.",
                result.summary()
        );

        assertEquals(
                LocalDate.of(2026, 6, 10),
                result.documentDate()
        );

        assertNull(result.specialty());
        assertNull(result.confidence());

        assertEquals(2, result.results().size());

        DocumentProcessingResultItemDTO firstResult =
                result.results().getFirst();

        assertEquals(
                "hemograma-result",
                firstResult.resultId()
        );

        assertEquals(
                "EXAME_HEMOGRAMA",
                firstResult.documentType()
        );

        Map<String, Object> data =
                firstResult.data();

        assertEquals(
                "HEMOGRAMA",
                data.get("exameTipo")
        );

        assertEquals(
                "Sangue total",
                data.get("material")
        );

        assertFalse(data.containsKey("id"));
        assertFalse(data.containsKey("patientId"));
        assertFalse(data.containsKey("documentType"));
        assertFalse(data.containsKey("documentDate"));
    }

    @Test
    void shouldAssembleStructuredFailure() {
        OutboxDocumentResponse outbox =
                completedOutbox(UUID.randomUUID());

        outbox.markFailedResponse(
                "AI_QUOTA_EXCEEDED",
                "O limite da IA foi excedido.",
                false
        );

        DocumentProcessingFailedDTO result =
                assembler.failed(outbox);

        assertEquals(
                outbox.getResponseEventId(),
                result.eventId()
        );

        assertEquals(
                outbox.getEventId(),
                result.correlationId()
        );

        assertEquals(
                "AI_QUOTA_EXCEEDED",
                result.error().code()
        );

        assertFalse(result.error().retryable());
    }

    private static OutboxDocumentResponse completedOutbox(
            UUID patientId
    ) {
        OutboxDocumentResponse outbox =
                new OutboxDocumentResponse();

        outbox.setEventId(UUID.randomUUID());
        outbox.setResponseEventId(UUID.randomUUID());
        outbox.setOccurredAt(
                Instant.parse(
                        "2026-07-22T18:30:00Z"
                )
        );
        outbox.setDocumentId(UUID.randomUUID());
        outbox.setPatientId(patientId);

        return outbox;
    }
}
