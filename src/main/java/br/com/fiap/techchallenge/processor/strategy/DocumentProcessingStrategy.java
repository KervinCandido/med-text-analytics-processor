package br.com.fiap.techchallenge.processor.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import java.time.LocalDateTime;

public interface DocumentProcessingStrategy {
    String getClassification();
    String getPromptInstruction();
    PanacheMongoEntity process(JsonNode dataNode, String fileName, String storedFilePath, LocalDateTime uploadTime);
}
