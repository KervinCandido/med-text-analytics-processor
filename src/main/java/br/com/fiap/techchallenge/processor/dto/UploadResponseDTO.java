package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJobStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDTO {
    private String id;
    private ProcessamentoJobStatus status;
}
