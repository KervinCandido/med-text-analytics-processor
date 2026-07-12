package br.com.fiap.techchallenge.processor.strategy;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class DocumentProcessingStrategyRegistry {

    private final Map<String, DocumentProcessingStrategy> strategyMap;

    @Inject
    public DocumentProcessingStrategyRegistry(Instance<DocumentProcessingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(DocumentProcessingStrategy::getClassification, Function.identity()));
    }

    public DocumentProcessingStrategy get(String classification) {
        return strategyMap.get(classification);
    }
}