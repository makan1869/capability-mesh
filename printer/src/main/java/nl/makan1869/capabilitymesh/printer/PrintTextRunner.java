package nl.makan1869.capabilitymesh.printer;

import com.anthropic.models.messages.OutputConfig;
import io.modelcontextprotocol.client.McpSyncClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nl.makan1869.capabilitymesh.printer.mcp.McpClientFactory;
import nl.makan1869.capabilitymesh.printer.registry.DiscoveredServer;
import nl.makan1869.capabilitymesh.printer.registry.RegistryClient;
import nl.makan1869.capabilitymesh.printer.tool.LoggingToolCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * On startup: discovers every MCP server registered in mcp-registry, opens a live MCP
 * client to each, hands the model their combined tool set, and asks it to perform the
 * configured print task (see TASK) - e.g. digits only come out of echo-printer's
 * unrestricted echo tool, since every other tool only emits letters.
 */
@Component
public class PrintTextRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PrintTextRunner.class);
    private static final String TASK = "Print ABC02";

    /**
     * Without this, the model tends to call tools for the letters it has a dedicated
     * tool for (a/b/c) and then just type any remaining characters (e.g. digits)
     * itself, since nothing stops it from answering directly. This forces every
     * character through a tool call, and points it at "echo" for anything that has
     * no dedicated per-character tool.
     */
    private static final String SYSTEM_INSTRUCTIONS =
            """
            You have a set of single-character "printer" tools. Most return one fixed
            letter (e.g. a tool named "a" always returns 'A'). The "echo" tool is
            different: it returns whatever single character you pass it, including
            digits - use it for any character that has no dedicated tool of its own.

            To complete the user's task, call exactly one tool per character of the
            requested text, in order, and build the final answer only from those tool
            results. Do not type any character of the final answer yourself - every
            character must come from a tool call result. Reply with only the final
            concatenated string, nothing else.
            """;

    private final RegistryClient registryClient;
    private final McpClientFactory mcpClientFactory;
    private final ChatClient chatClient;
    private final PrinterProperties properties;

    public PrintTextRunner(
            RegistryClient registryClient,
            McpClientFactory mcpClientFactory,
            ChatModel chatModel,
            PrinterProperties properties) {
        this.registryClient = registryClient;
        this.mcpClientFactory = mcpClientFactory;
        this.chatClient = ChatClient.create(chatModel);
        this.properties = properties;
    }

    @Override
    public void run(String... args) {
        List<DiscoveredServer> discoveredServers = registryClient.listServers(properties.registry().url());
        log.info("Discovered {} MCP server(s) from the registry: {}", discoveredServers.size(), discoveredServers);

        List<McpSyncClient> mcpClients = new ArrayList<>();
        try {
            for (DiscoveredServer server : discoveredServers) {
                mcpClients.add(mcpClientFactory.connect(server));
            }

            ToolCallbackProvider mcpToolCallbacks =
                    SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build();

            List<String> toolCallLog = Collections.synchronizedList(new ArrayList<>());
            ToolCallback[] loggingCallbacks = Arrays.stream(mcpToolCallbacks.getToolCallbacks())
                    .map(callback -> (ToolCallback) new LoggingToolCallback(callback, toolCallLog))
                    .toArray(ToolCallback[]::new);
            ToolCallbackProvider toolCallbackProvider = ToolCallbackProvider.from(loggingCallbacks);

            String result = chatClient
                    .prompt()
                    .system(SYSTEM_INSTRUCTIONS)
                    .user(TASK)
                    .tools(toolCallbackProvider)
                    .options(AnthropicChatOptions.builder()
                            .effort(OutputConfig.Effort.of(properties.anthropic().effort().toLowerCase())))
                    .call()
                    .content();

            log.info("Task: \"{}\" -> Model result: {}", TASK, result);
            log.info(
                    "Tools called by the model ({} call(s)): {}",
                    toolCallLog.size(),
                    toolCallLog.isEmpty() ? "none" : String.join(", ", toolCallLog));
        } finally {
            mcpClients.forEach(McpSyncClient::closeGracefully);
        }
    }
}
