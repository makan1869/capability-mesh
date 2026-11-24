package nl.makan1869.capabilitymesh.printer.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a ToolCallback to require and log a brief selection reason for every
 * invocation, while keeping the synthetic reason argument away from the delegate.
 */
public class LoggingToolAndReasonsCallback implements ToolCallback {

    private static final Logger log = LoggerFactory.getLogger(LoggingToolAndReasonsCallback.class);
    private static final String REASON_PROPERTY = "reason";
    private static final TypeReference<Map<String, Object>> JSON_OBJECT_TYPE = new TypeReference<>() {};

    private final ToolCallback delegate;
    private final List<String> callLog;
    private final ToolDefinition toolDefinition;
    private static final JsonMapper jsonMapper = JsonMapper.builder().build();

    public LoggingToolAndReasonsCallback(ToolCallback delegate, List<String> callLog) {
        this.delegate = delegate;
        this.callLog = callLog;
        ToolDefinition originalDefinition = delegate.getToolDefinition();
        this.toolDefinition = ToolDefinition.builder()
                .name(originalDefinition.name())
                .description(originalDefinition.description())
                .inputSchema(addReasonToSchema(originalDefinition.inputSchema()))
                .build();
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        PreparedInvocation invocation = prepareInvocation(toolInput);
        return record(invocation, () -> delegate.call(invocation.delegateInput()));
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        PreparedInvocation invocation = prepareInvocation(toolInput);
        return record(invocation, () -> delegate.call(invocation.delegateInput(), toolContext));
    }

    private String addReasonToSchema(String inputSchema) {
        Map<String, Object> schema = inputSchema == null || inputSchema.isBlank()
                ? new LinkedHashMap<>()
                : jsonMapper.readValue(inputSchema, JSON_OBJECT_TYPE);
        if (schema == null) {
            throw new IllegalArgumentException("Tool input schema must be a JSON object.");
        }
        Object typeValue = schema.get("type");
        if (typeValue == null) {
            schema.put("type", "object");
        } else if (!"object".equals(typeValue)) {
            throw new IllegalArgumentException("Tool input schema must describe a JSON object.");
        }

        Object propertiesValue = schema.get("properties");
        Map<String, Object> properties = new LinkedHashMap<>();
        if (propertiesValue != null) {
            if (!(propertiesValue instanceof Map<?, ?> existingProperties)) {
                throw new IllegalArgumentException("Tool input schema properties must be a JSON object.");
            }
            existingProperties.forEach((key, value) -> {
                if (!(key instanceof String propertyName)) {
                    throw new IllegalArgumentException("Tool input schema property names must be strings.");
                }
                properties.put(propertyName, value);
            });
        }
        if (properties.containsKey(REASON_PROPERTY)) {
            throw new IllegalArgumentException("Tool input schema already contains the reserved 'reason' property.");
        }
        properties.put(REASON_PROPERTY, Map.of(
                "type", "string",
                "description", "Briefly explain why this tool was selected."));
        schema.put("properties", properties);

        Object requiredValue = schema.get("required");
        List<Object> required = new ArrayList<>();
        if (requiredValue != null) {
            if (!(requiredValue instanceof List<?> existingRequired)) {
                throw new IllegalArgumentException("Tool input schema required must be a JSON array.");
            }
            required.addAll(existingRequired);
        }
        if (!required.contains(REASON_PROPERTY)) {
            required.add(REASON_PROPERTY);
        }
        schema.put("required", required);
        return jsonMapper.writeValueAsString(schema);
    }

    private PreparedInvocation prepareInvocation(String toolInput) {
        Map<String, Object> arguments = jsonMapper.readValue(toolInput, JSON_OBJECT_TYPE);
        if (arguments == null) {
            throw new IllegalArgumentException("Tool input must be a JSON object containing a non-blank 'reason'.");
        }
        Object reasonValue = arguments.remove(REASON_PROPERTY);
        if (!(reasonValue instanceof String reason) || reason.isBlank()) {
            throw new IllegalArgumentException("Tool input must include a non-blank 'reason'.");
        }
        return new PreparedInvocation(reason, jsonMapper.writeValueAsString(arguments));
    }

    private String record(PreparedInvocation preparedInvocation, java.util.function.Supplier<String> invocation) {
        String name = delegate.getToolDefinition().name();
        log.info(
                "Tool call -> {} (reason: {}) with {}",
                name,
                preparedInvocation.reason(),
                preparedInvocation.delegateInput());
        String result = invocation.get();
        log.info("Tool call <- {} = {}", name, result);
        callLog.add("%s(reason=%s, %s) -> %s".formatted(
                name, preparedInvocation.reason(), preparedInvocation.delegateInput(), result));
        return result;
    }

    private record PreparedInvocation(String reason, String delegateInput) {}
}
