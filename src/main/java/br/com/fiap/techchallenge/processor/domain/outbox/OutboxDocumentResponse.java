package br.com.fiap.techchallenge.processor.domain.outbox;

import br.com.fiap.techchallenge.processor.domain.ProcessingStatus;
import br.com.fiap.techchallenge.processor.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OutboxDocumentResponse {

    private static final String STORAGE_READ_ERROR =
            "STORAGE_READ_ERROR";

    private static final String AI_PROCESSING_ERROR =
            "AI_PROCESSING_ERROR";

    private static final String AI_QUOTA_EXCEEDED =
            "AI_QUOTA_EXCEEDED";

    private static final String FALLBACK_ERROR_CODE =
            "PROCESSING_ERROR";

    private static final String STORAGE_READ_ERROR_MESSAGE =
            "Não foi possível ler o arquivo armazenado.";

    private static final String AI_PROCESSING_ERROR_MESSAGE =
            "Não foi possível processar o documento "
                    + "pela inteligência artificial.";

    private static final String AI_QUOTA_EXCEEDED_MESSAGE =
            "O limite de uso do serviço de inteligência "
                    + "artificial foi excedido.";

    private static final String FALLBACK_ERROR_MESSAGE =
            "Não foi possível processar o documento.";

    private String outboxId;

    /*
     * Estado de entrega do evento de Outbox:
     * PENDING, PROCESSING, PROCESSED ou FAILED.
     */
    private ProcessingStatus status;

    /*
     * Estado comunicado ao Patient Document Service:
     * PROCESSED ou FAILED.
     */
    private ProcessingStatus responseStatus;

    private UUID eventId;
    private UUID documentId;
    private UUID patientId;

    private LocalDateTime createdAt;

    /*
     * Momento em que o resultado terminal foi produzido.
     * Deve permanecer estável durante novas tentativas.
     */
    private Instant occurredAt;

    private List<String> documents;

    private String errorCode;
    private String errorMessage;
    private Boolean errorRetryable;

    /*
     * Campo legado mantido temporariamente durante a migração.
     */
    private String errorDetail;

    public OutboxDocumentResponse() {
        this.status = ProcessingStatus.PENDING;
        this.responseStatus = ProcessingStatus.PROCESSED;
        this.documents = new ArrayList<>();
        this.createdAt = LocalDateTime.now(
                Constants.SAO_PAULO_ZONE_ID
        );
    }

    public void addDocumentId(String documentoId) {
        this.documents.add(documentoId);
    }

    public void markSuccessfulResponse() {
        this.responseStatus = ProcessingStatus.PROCESSED;

        markResponseOccurredNow();

        this.errorCode = null;
        this.errorMessage = null;
        this.errorRetryable = null;
        this.errorDetail = null;
    }

    public void markFailedResponse(
            String errorCode,
            String errorMessage,
            boolean errorRetryable
    ) {
        this.responseStatus = ProcessingStatus.FAILED;

        markResponseOccurredNow();

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorRetryable = errorRetryable;

        this.errorDetail = truncate(
                errorCode + ": " + errorMessage,
                2000
        );

        this.documents.clear();
    }

    /*
     * Compatibilidade temporária com chamadas e documentos legados.
     */
    @Deprecated
    public void markFailedResponse(String errorDetail) {
        this.responseStatus = ProcessingStatus.FAILED;

        markResponseOccurredNow();

        this.errorCode = null;
        this.errorMessage = null;
        this.errorRetryable = null;
        this.errorDetail = truncate(errorDetail, 2000);

        this.documents.clear();
    }

    /*
     * Preenche occurredAt para documentos antigos do Mongo.
     */
    public void ensureOccurredAt() {
        if (this.occurredAt != null) {
            return;
        }

        if (this.createdAt != null) {
            this.occurredAt = this.createdAt
                    .atZone(Constants.SAO_PAULO_ZONE_ID)
                    .toInstant();

            return;
        }

        this.occurredAt = Instant.now();
    }

    /*
     * Converte uma falha legada no formato "CODIGO: mensagem"
     * para o erro estruturado obrigatório no contrato v1.
     */
    public void ensureStructuredError() {
        if (!isFailedResponse()) {
            return;
        }

        String legacyErrorCode =
                extractLegacyErrorCode();

        if (!isKnownErrorCode(this.errorCode)) {
            this.errorCode = isKnownErrorCode(
                    legacyErrorCode
            )
                    ? legacyErrorCode
                    : FALLBACK_ERROR_CODE;

            /*
             * Mensagens legadas não são reutilizadas porque podem
             * conter stack traces ou detalhes internos.
             */
            this.errorMessage = safeMessageFor(
                    this.errorCode
            );
        } else if (isBlank(this.errorMessage)) {
            this.errorMessage = safeMessageFor(
                    this.errorCode
            );
        }

        this.errorMessage = truncate(
                this.errorMessage,
                1000
        );

        if (this.errorRetryable == null) {
            this.errorRetryable = inferRetryable(
                    this.errorCode
            );
        }

        /*
         * Sobrescreve o detalhe legado com conteúdo sanitizado.
         */
        this.errorDetail = truncate(
                this.errorCode + ": " + this.errorMessage,
                2000
        );
    }

    public boolean isFailedResponse() {
        return ProcessingStatus.FAILED.equals(
                responseStatus
        );
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

    private void markResponseOccurredNow() {
        if (this.occurredAt == null) {
            this.occurredAt = Instant.now();
        }
    }

    private String extractLegacyErrorCode() {
        if (isBlank(this.errorDetail)) {
            return null;
        }

        int separator = this.errorDetail.indexOf(':');

        if (separator <= 0) {
            return null;
        }

        String candidate = this.errorDetail
                .substring(0, separator)
                .trim();

        return isKnownErrorCode(candidate)
                ? candidate
                : null;
    }

    private static String safeMessageFor(
            String errorCode
    ) {
        return switch (errorCode) {
            case STORAGE_READ_ERROR ->
                    STORAGE_READ_ERROR_MESSAGE;

            case AI_PROCESSING_ERROR ->
                    AI_PROCESSING_ERROR_MESSAGE;

            case AI_QUOTA_EXCEEDED ->
                    AI_QUOTA_EXCEEDED_MESSAGE;

            default -> FALLBACK_ERROR_MESSAGE;
        };
    }

    private static boolean inferRetryable(String errorCode) {
        return STORAGE_READ_ERROR.equals(errorCode)
                || AI_PROCESSING_ERROR.equals(errorCode);
    }

    private static boolean isKnownErrorCode(String value) {
        return STORAGE_READ_ERROR.equals(value)
                || AI_PROCESSING_ERROR.equals(value)
                || AI_QUOTA_EXCEEDED.equals(value)
                || FALLBACK_ERROR_CODE.equals(value);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String truncate(
            String value,
            int maximumLength
    ) {
        if (value == null
                || value.length() <= maximumLength) {
            return value;
        }

        return value.substring(0, maximumLength);
    }
}
