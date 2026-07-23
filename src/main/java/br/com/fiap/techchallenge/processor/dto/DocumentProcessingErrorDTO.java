package br.com.fiap.techchallenge.processor.dto;

public record DocumentProcessingErrorDTO(
        String code,
        String message,
        boolean retryable
) {

    public DocumentProcessingErrorDTO {
        if (code == null
                || code.isBlank()
                || code.length() > 100
                || !code.matches("^[A-Z][A-Z0-9_]*$")) {
            throw new IllegalArgumentException(
                    "Código de erro inválido."
            );
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Mensagem de erro é obrigatória."
            );
        }

        if (message.length() > 2000) {
            throw new IllegalArgumentException(
                    "Mensagem de erro deve possuir "
                            + "até 2000 caracteres."
            );
        }
    }
}
