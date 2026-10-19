package nl.makan1869.capabilitymesh.printer.tool;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;

/**
 * Wraps a ToolCallback so every invocation is logged (and recorded into callLog),
 * making it possible to see exactly which tools the model chose to call, in what
 * order, with what input, and what each one returned.
 */
public class LoggingToolCallback implements ToolCallback {

    private static final Logger log = LoggerFactory.getLogger(LoggingToolCallback.class);

    private final ToolCallback delegate;
    private final List<String> callLog;

    public LoggingToolCallback(ToolCallback delegate, List<String> callLog) {
        this.delegate = delegate;
        this.callLog = callLog;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return record(toolInput, () -> delegate.call(toolInput));
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return record(toolInput, () -> delegate.call(toolInput, toolContext));
    }

    private String record(String toolInput, java.util.function.Supplier<String> invocation) {
        String name = delegate.getToolDefinition().name();
        log.info("Tool call -> {}({})", name, toolInput);
        String result = invocation.get();
        log.info("Tool call <- {}({}) = {}", name, toolInput, result);
        callLog.add("%s(%s) -> %s".formatted(name, toolInput, result));
        return result;
    }
}
