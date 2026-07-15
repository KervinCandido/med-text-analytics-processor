package br.com.fiap.techchallenge.processor.persistence.entity.receita;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import br.com.fiap.techchallenge.processor.persistence.entity.DocumentEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "receitas")
@Getter
@Setter
@NoArgsConstructor
public class ReceitaEntity extends DocumentEntity {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private List<ReceitaItemEntity> itens;
    private List<String> observacoes;
    private List<String> notas;
    private LocalDateTime dataReceita;
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> resolveDocumentDate() {
        return Optional.ofNullable(this.dataReceita);
    }
}
