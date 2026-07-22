package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import br.com.fiap.techchallenge.processor.domain.Documento;
import br.com.fiap.techchallenge.processor.domain.outbox.OutboxDocumentResponse;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingCompletedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingErrorDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingFailedDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultDTO;
import br.com.fiap.techchallenge.processor.dto.DocumentProcessingResultItemDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class DocumentProcessingResultAssembler {

    private static final Set<String> INTERNAL_FIELDS =
            Set.of(
                    "id",
                    "patientId",
                    "documentType",
                    "documentDate"
            );

    private static final TypeReference<
            LinkedHashMap<String, Object>
            > DATA_MAP_TYPE =
            new TypeReference<>() {
            };

    private static final Comparator<Documento>
            DOCUMENT_ORDER =
            Comparator.comparing(
                    (Documento document) ->
                            document.getDocumentType().name()
            ).thenComparing(
                    Documento::getId
            );

    private final ObjectMapper objectMapper;

    @Inject
    public DocumentProcessingResultAssembler(
            ObjectMapper objectMapper
    ) {
        this.objectMapper =
                Objects.requireNonNull(
                        objectMapper,
                        "objectMapper é obrigatório."
                );
    }

    public DocumentProcessingCompletedDTO completed(
            OutboxDocumentResponse outbox,
            List<Documento> documents
    ) {
        requireOutbox(outbox);

        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException(
                    "documents deve possuir ao menos um item."
            );
        }

        List<Documento> orderedDocuments =
                new ArrayList<>(documents);

        orderedDocuments.forEach(
                DocumentProcessingResultAssembler
                        ::validateDocument
        );

        orderedDocuments.sort(DOCUMENT_ORDER);

        List<DocumentProcessingResultItemDTO> results =
                orderedDocuments.stream()
                        .map(this::toResultItem)
                        .toList();

        LocalDate documentDate =
                results.stream()
                        .map(
                                DocumentProcessingResultItemDTO
                                        ::documentDate
                        )
                        .filter(Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElse(null);

        return new DocumentProcessingCompletedDTO(
                DocumentProcessingResultDTO
                        .CURRENT_SCHEMA_VERSION,
                DocumentProcessingResultDTO
                        .DOCUMENT_PROCESSING_COMPLETED,
                outbox.getResponseEventId(),
                outbox.getEventId(),
                outbox.getOccurredAt(),
                outbox.getDocumentId(),
                outbox.getPatientId(),
                createSummary(orderedDocuments),
                resolvePrimaryDocumentType(
                        orderedDocuments
                                .getFirst()
                                .getDocumentType()
                ),
                null,
                documentDate,
                null,
                results
        );
    }

    public DocumentProcessingFailedDTO failed(
            OutboxDocumentResponse outbox
    ) {
        requireOutbox(outbox);

        return new DocumentProcessingFailedDTO(
                DocumentProcessingResultDTO
                        .CURRENT_SCHEMA_VERSION,
                DocumentProcessingResultDTO
                        .DOCUMENT_PROCESSING_FAILED,
                outbox.getResponseEventId(),
                outbox.getEventId(),
                outbox.getOccurredAt(),
                outbox.getDocumentId(),
                outbox.getPatientId(),
                new DocumentProcessingErrorDTO(
                        outbox.getErrorCode(),
                        outbox.getErrorMessage(),
                        Boolean.TRUE.equals(
                                outbox.getErrorRetryable()
                        )
                )
        );
    }

    private DocumentProcessingResultItemDTO toResultItem(
            Documento document
    ) {
        LinkedHashMap<String, Object> data =
                objectMapper.convertValue(
                        document,
                        DATA_MAP_TYPE
                );

        data.entrySet().removeIf(
                entry ->
                        INTERNAL_FIELDS.contains(
                                entry.getKey()
                        )
                                || entry.getValue() == null
        );

        LocalDate documentDate =
                document.getDocumentDate() == null
                        ? null
                        : document.getDocumentDate()
                                .toLocalDate();

        return new DocumentProcessingResultItemDTO(
                document.getId(),
                document.getDocumentType().name(),
                documentDate,
                data
        );
    }

    private static String createSummary(
            List<Documento> documents
    ) {
        String documentTypes =
                documents.stream()
                        .map(Documento::getDocumentType)
                        .map(
                                DocumentProcessingResultAssembler
                                        ::humanize
                        )
                        .distinct()
                        .collect(
                                Collectors.joining(", ")
                        );

        return "Documento processado contendo: "
                + documentTypes
                + ".";
    }

    private static String resolvePrimaryDocumentType(
            DocumentType documentType
    ) {
        String typeName = documentType.name();

        if (typeName.startsWith("EXAME_")) {
            return "EXAME_LABORATORIAL";
        }

        if (typeName.startsWith("LAUDO_")) {
            return "LAUDO";
        }

        return typeName;
    }

    private static String humanize(
            DocumentType documentType
    ) {
        return documentType.name()
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ');
    }

    private static void validateDocument(
            Documento document
    ) {
        if (document == null) {
            throw new IllegalArgumentException(
                    "documents não pode conter item nulo."
            );
        }

        if (document.getId() == null
                || document.getId().isBlank()) {
            throw new IllegalArgumentException(
                    "Documento processado sem id."
            );
        }

        if (document.getDocumentType() == null) {
            throw new IllegalArgumentException(
                    "Documento processado sem documentType."
            );
        }
    }

    private static void requireOutbox(
            OutboxDocumentResponse outbox
    ) {
        if (outbox == null) {
            throw new IllegalArgumentException(
                    "outbox é obrigatório."
            );
        }
    }
}
