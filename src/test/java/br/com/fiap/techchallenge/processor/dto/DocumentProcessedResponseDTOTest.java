package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.outros.Outros;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentProcessedResponseDTOTest {

    @Test
    void shouldAcceptProcessedDocumentIdWith64Characters() {
        Outros document = documentWithId(
                "a".repeat(64)
        );

        assertDoesNotThrow(
                () -> DocumentProcessedResponseDTO.processed(
                        UUID.randomUUID(),
                        Instant.now(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        document
                )
        );
    }

    @Test
    void shouldRejectProcessedResponseWithoutDocumentId() {
        Outros document = documentWithId(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> DocumentProcessedResponseDTO.processed(
                        UUID.randomUUID(),
                        Instant.now(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        document
                )
        );
    }

    @Test
    void shouldRejectProcessedDocumentIdWith65Characters() {
        Outros document = documentWithId(
                "a".repeat(65)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> DocumentProcessedResponseDTO.processed(
                        UUID.randomUUID(),
                        Instant.now(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        document
                )
        );
    }

    @Test
    void shouldRejectInvalidStructuredErrorCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DocumentProcessingErrorDTO(
                        "invalid-code",
                        "Mensagem segura.",
                        true
                )
        );
    }

    private Outros documentWithId(String id) {
        Outros document = new Outros();
        document.setId(id);
        return document;
    }
}
