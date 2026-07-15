package br.com.fiap.techchallenge.processor.dto;

import br.com.fiap.techchallenge.processor.domain.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetaDataDTO {
    private List<DocumentType> classifications;
}
