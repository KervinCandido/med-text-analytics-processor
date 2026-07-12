package br.com.fiap.techchallenge.processor.domain.exame;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


import br.com.fiap.techchallenge.processor.domain.Document;
import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "exames")
@Getter
@Setter
@NoArgsConstructor
public class Exame extends Document {
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;
    
    private String exameTipo; // e.g. "HEMOGRAMA", "LIPIDOGRAMA", "OUTRO"
    private String material;  // e.g. "Soro", "Sangue total"
    private String metodo;    // e.g. "Enzimático", "Automatizado"
    private LocalDateTime dataColeta;     // Data/hora da coleta do exame
    private LocalDateTime dataLiberacao;  // Data/hora de liberação do resultado
    private List<String> observacoes; // Lista de observações do exame
    private List<String> notas;       // Lista de notas do exame
    private String descricaoGeral;

    @Override
    protected Optional<LocalDateTime> documentDate() {
        return Optional.ofNullable(this.dataColeta != null ? this.dataColeta : this.dataLiberacao);
    }
}
