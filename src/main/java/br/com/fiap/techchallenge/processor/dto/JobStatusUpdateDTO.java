package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJobStatus;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusUpdateDTO {
    private String id;
    private ProcessamentoJobStatus status;
    private int processedFiles;
    private List<String> imagensSucesso;
    private List<String> imagensFalha;
    private List<String> resultados;
    private String errorDetail;
}
