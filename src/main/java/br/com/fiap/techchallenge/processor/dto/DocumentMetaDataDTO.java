package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetaDataDTO {
    private List<DocumentType> classifications;
}
