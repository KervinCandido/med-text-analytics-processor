package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJobStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusResponseDTO {
    private String id;
    private ProcessamentoJobStatus status;
    private int totalFiles;
    private int processedFiles;
    private int retryCount;
    private List<String> imagensSucesso;
    private List<String> imagensFalha;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String errorDetail;
    private List<JsonNode> resultados;
}
