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
public class InboxEventDTO {
    private String Id;
    private List<String> filePaths;
    private String userId;
    private int totalFiles;
}
