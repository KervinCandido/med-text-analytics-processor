package br.com.fiap.techchallenge.processor.service;

import br.com.fiap.techchallenge.processor.dto.DocumentMetaDataDTO;
import br.com.fiap.techchallenge.processor.strategy.DocumentProcessingStrategyRegistry;
import br.com.fiap.techchallenge.processor.strategy.DocumentProcessingStrategy;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;
import java.nio.file.Files;

@ApplicationScoped
public class AiVisionService {

    private static final Logger logger = LoggerFactory.getLogger(AiVisionService.class);

    private final ChatModel classifierModel;
    private final ChatModel extractorModel;
    private final DocumentProcessingStrategyRegistry strategyRegistry;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    @Inject
    public AiVisionService(
            @ConfigProperty(name = "app.ai.gemini.api-key") String apiKey,
            @ConfigProperty(name = "app.ai.gemini.model-classifier") String classifierModelName,
            @ConfigProperty(name = "app.ai.gemini.model-extractor") String extractorModelName,
            DocumentProcessingStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;

        this.classifierModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(classifierModelName)
                .temperature(0.1)
                .build();

        this.extractorModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(extractorModelName)
                .temperature(0.2)
                .build();

        // 1. Retry config: max 4 attempts, exponential backoff starting at 10000ms, multiplier 2.0x, jitter 0.5 (random +/- 50% delay)
        IntervalFunction intervalFn = IntervalFunction.ofExponentialRandomBackoff(10000, 2.0d, 0.5d);
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(4)
                .intervalFunction(intervalFn)
                .retryExceptions(Exception.class)
                .ignoreExceptions(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class) // Do not retry if CB is open
                .build();
        this.retry = Retry.of("gemini-api-retry", retryConfig);

        // 2. Circuit Breaker config: opens if >50% failure rate in 6 calls, wait 30s in Open state before Half-Open
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f)
                .slidingWindowSize(6)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(2)
                .recordExceptions(Exception.class)
                .build();
        this.circuitBreaker = CircuitBreaker.of("gemini-api-circuitbreaker", cbConfig);
    }

    public String classifyDocument(String filePath) {
        try {
            // Chaining: outer is Retry, inner is CircuitBreaker
            return Retry.decorateCheckedSupplier(retry,
                CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
                    logger.info("action=classifyDocumentAiCallStart, filePath={}", filePath);
                    Path path = Paths.get(filePath);
                    byte[] fileContent = Files.readAllBytes(path);
                    String base64Image = Base64.getEncoder().encodeToString(fileContent);
                    String mimeType = getMimeType(filePath);

                    ImageContent imageContent = ImageContent.from(base64Image, mimeType);
                    TextContent textContent = TextContent.from(
                            "Analise esta imagem médica. Identifique todos os documentos ou tipos de exames que aparecem na imagem.\n" +
                            "Uma única imagem pode conter mais de um exame. Classifique-os gerando uma lista com as categorias correspondentes:\n" +
                            "- EXAME_HEMOGRAMA (se contiver hemograma completo)\n" +
                            "- EXAME_LIPIDOGRAMA (se contiver perfil lipídico / lipidograma)\n" +
                            "- EXAME_GLICEMIA_JEJUM (se contiver exame de glicemia de jejum)\n" +
                            "- EXAME_HEMOGLOBINA_GLICADA (se contiver exame de hemoglobina glicada / HbA1c)\n" +
                            "- EXAME_TSH (se contiver exame de TSH / Hormônio Tireoestimulante)\n" +
                            "- EXAME_T4_LIVRE (se contiver exame de T4 Livre ou Tiroxina Livre)\n" +
                            "- EXAME_BETA_HCG (se contiver exame de Beta-HCG / Teste de Gravidez)\n" +
                            "- EXAME_OUTROS (outro tipo de exame laboratorial ou de imagem não listado acima)\n" +
                            "- RECEITA (receituário médico de medicamentos)\n" +
                            "- LAUDO_UST_ABDOME_TOTAL (laudo de ultrassonografia do abdome total)\n" +
                            "- LAUDO_US_OBSTETRICO_ENDOVAGINAL (laudo de ultrassom obstétrico endovaginal)\n" +
                            "- LAUDO_US_OBSTETRICO_DOPPLER (laudo de ultrassom obstétrico com doppler)\n" +
                            "- LAUDO_US_PELVICA_TRANSVAGINAL (laudo de ultrassom pélvico transvaginal)\n" +
                            "- LAUDO_RESSONANCIA_MAGNETICA (laudo de ressonância magnética de qualquer parte do corpo, ex: crânio, ombro, joelho, coluna)\n" +
                            "- LAUDO_TOMOGRAFIA_COMPUTADORIZADA (laudo de tomografia computadorizada de qualquer parte do corpo, ex: tórax, abdome, crânio)\n" +
                            "- LAUDO_MAMOGRAFIA (laudo de mamografia / radiologia mamária)\n" +
                            "- LAUDO_ENDOSCOPIA_DIGESTIVA_ALTA (laudo de endoscopia digestiva alta)\n" +
                            "- LAUDO_COLONOSCOPIA (laudo de colonoscopia)\n" +
                            "- LAUDO_OUTROS (outros laudos médicos de exames ou cirurgias não listados acima - NÃO classifique folhas de resultados de exames de laboratório comuns como laudo apenas por conter assinaturas ou identificações)\n" +
                            "- RELATORIO (relatório médico ou evolução clínica)\n" +
                            "- ENCAMINHAMENTO (guia de encaminhamento para outro profissional)\n" +
                            "- REGISTRO_ATENDIMENTO (registro de atendimento em pronto-socorro ou ficha de consulta)\n" +
                            "- OUTROS (caso não seja possível identificar ou se não for um documento médico)\n\n" +
                            "Atenção especial: O exame \"Tiroxina Livre\" deve ser classificado as \"EXAME_T4_LIVRE\". Não duplique a classificação se houver informações de cabeçalhos de laboratório.\n\n" +
                            "Retorne o resultado exatamente no seguinte formato JSON, contendo um array de classificações, sem markdown ou texto adicional:\n" +
                            "{\"classifications\": [\"CLASSIFICACAO_1\", \"CLASSIFICACAO_2\"]}"
                    );

                    UserMessage userMessage = UserMessage.from(textContent, imageContent);
                    dev.langchain4j.model.chat.response.ChatResponse response = classifierModel.chat(userMessage);
                    return cleanJson(response.aiMessage().text());
                })
            ).get();
        } catch (Throwable t) {
            logger.error("action=classifyDocumentAiCallFailed, filePath={}, error={}", filePath, t.getMessage());
            throw new RuntimeException("Failed to classify document with AI after retries and circuit breaker", t);
        }
    }

    public String extractDocumentData(String filePath, String classification) {
        try {
            // Chaining: outer is Retry, inner is CircuitBreaker
            return Retry.decorateCheckedSupplier(retry,
                CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
                    logger.info("action=extractDocumentDataAiCallStart, filePath={}, classification={}", filePath, classification);
                    Path path = Paths.get(filePath);
                    byte[] fileContent = Files.readAllBytes(path);
                    String base64Image = Base64.getEncoder().encodeToString(fileContent);
                    String mimeType = getMimeType(filePath);

                    ImageContent imageContent = ImageContent.from(base64Image, mimeType);
                    TextContent textContent;

                    DocumentProcessingStrategy strategy = strategyRegistry.get(classification);
                    
                    String privacyWarning = "\n\n=== REGRAS CRÍTICAS DE PRIVACIDADE E ANOMINIZAÇÃO ===\n" +
                            "Para conformidade com leis de proteção de dados, NÃO INCLUA em nenhuma circunstância nomes de pessoas físicas " +
                            "(como paciente, médico, etc.), nomes de clínicas, laboratórios, hospitais, locais de atendimento, números de registro profissional " +
                            "(como CRM, etc.), CPFs, RGs ou endereços físicos nos campos de texto livre preenchidos no JSON " +
                            "(especialmente em 'descricaoGeral', 'observacoes', 'notas', etc.).\n" +
                            "Caso precise descrever alguma ação ou local, substitua os nomes específicos por termos totalmente neutros e genéricos " +
                            "(ex: substitua 'Maria Lopes Ramos' por 'o paciente'; substitua 'Pronto Atendimento Dr. José Lins' por 'o pronto atendimento' ou 'a instituição emissora').";

                    if (strategy != null) {
                        textContent = TextContent.from(strategy.getPromptInstruction() + privacyWarning);
                    } else {
                        textContent = TextContent.from(
                                "Analise esta imagem em português e forneça uma descrição geral da imagem no seguinte formato JSON:\n" +
                                "{\n" +
                                "  \"descricaoGeral\": \"Descrição geral detalhada do conteúdo da imagem.\"\n" +
                                "}\n" +
                                "Retorne apenas o JSON, sem markdown ou texto adicional." + privacyWarning
                        );
                    }

                    UserMessage userMessage = UserMessage.from(textContent, imageContent);
                    dev.langchain4j.model.chat.response.ChatResponse response = extractorModel.chat(userMessage);
                    return cleanJson(response.aiMessage().text());
                })
            ).get();
        } catch (Throwable t) {
            logger.error("action=extractDocumentDataAiCallFailed, filePath={}, classification={}, error={}", filePath, classification, t.getMessage());
            throw new RuntimeException("Failed to extract data with AI after retries and circuit breaker", t);
        }
    }

    private String cleanJson(String rawText) {
        String jsonResult = rawText.trim();
        if (jsonResult.startsWith("```json")) {
            jsonResult = jsonResult.substring(7);
        }
        if (jsonResult.startsWith("```")) {
            jsonResult = jsonResult.substring(3);
        }
        if (jsonResult.endsWith("```")) {
            jsonResult = jsonResult.substring(0, jsonResult.length() - 3);
        }
        return jsonResult.trim();
    }

    private String getMimeType(String filePath) {
        String lower = filePath.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) return "image/jpeg";
        return "application/octet-stream";
    }
}
