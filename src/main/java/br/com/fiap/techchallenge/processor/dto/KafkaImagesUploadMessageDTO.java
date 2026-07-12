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
public class KafkaImagesUploadMessageDTO {
    private String id;
    private List<String> imageUrls;
    private String userId;
    private int totalFiles;
}
