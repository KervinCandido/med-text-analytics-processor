package br.com.fiap.techchallenge.processor.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseStrategy implements DocumentProcessingStrategy {
    protected Double getNullableDouble(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asDouble();
        }
        return null;
    }

    protected String getNullableText(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return null;
    }

    protected LocalDateTime getNullableDateTime(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            try {
                String val = node.get(fieldName).asText().trim();
                if (!val.isEmpty()) {
                    return LocalDateTime.parse(val);
                }
            } catch (Exception e) {
                System.err.println("Failed to parse date '" + node.get(fieldName).asText() + "' for field " + fieldName);
            }
        }
        return null;
    }

    protected List<String> getList(JsonNode node, String fieldName) {
        List<String> list = new ArrayList<>();
        if (node != null && node.has(fieldName) && node.get(fieldName).isArray()) {
            for (JsonNode item : node.get(fieldName)) {
                list.add(item.asText());
            }
        }
        return list;
    }
}
