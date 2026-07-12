package br.com.fiap.techchallenge.processor.domain.job;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "processamentos")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ProcessamentoJob extends PanacheMongoEntityBase {

    private static final ZoneId SAO_PAULO_ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @BsonId
    private String id; // UUID
    private ProcessamentoJobStatus status; // PENDENTE_PROCESSAMENTO, PROCESSANDO, PROCESSADO, ERRO_NO_PROCESSAMENTO
    private String userId; // Owner user ID
    private int totalFiles;
    private int processedFiles;
    private int retryCount; // Number of times the job was retried by the scheduler
    private String errorDetail;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<String> imagensSucesso; // List of image file paths processed successfully
    private List<String> imagensFalha;   // List of image file paths that failed processing
    private List<String> resultados; // JSON strings representing the persisted entities

    public ProcessamentoJob() {
        this.processedFiles = 0;
        this.retryCount = 0;
        this.imagensSucesso = new ArrayList<>();
        this.imagensFalha = new ArrayList<>();
        this.resultados = new ArrayList<>();
        this.createdAt = LocalDateTime.now(SAO_PAULO_ZONE_ID);
        this.status = br.com.fiap.techchallenge.processor.domain.job.ProcessamentoJobStatus.PENDENTE_PROCESSAMENTO;
    }

    public static Optional<ProcessamentoJob> findById(String id) {
        return find("id", id).firstResultOptional();
    }

    public void addResultados(List<String> persistedEntitiesJson) {
        this.resultados.addAll(persistedEntitiesJson);
    }

    public void incrementeProcessedFiles() {
        this.processedFiles++;
    }

    public void addImagensSucesso(String storedFilePath) {
        this.imagensSucesso.add(storedFilePath);
    }

    public void addImagensFalha(String storedFilePath) {
        this.imagensFalha.add(storedFilePath);
    }

    public void finish() {
        setStatus(this.imagensFalha.isEmpty() ? ProcessamentoJobStatus.PROCESSADO : ProcessamentoJobStatus.ERRO_NO_PROCESSAMENTO);
        this.completedAt = LocalDateTime.now(SAO_PAULO_ZONE_ID);
    }

    public void retryIncrement() {
        this.retryCount++;
    }

    public void limpaFalhas() {
        this.imagensFalha.clear();
    }
}
