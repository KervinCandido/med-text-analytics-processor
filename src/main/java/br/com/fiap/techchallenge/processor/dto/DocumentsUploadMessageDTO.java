package br.com.fiap.techchallenge.processor.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsUploadMessageDTO {
    private String documentsUploadId;
    private List<String> fileUrls;
    private String patientId;
}
