package nl.makan1869.capabilitymesh.printer;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "printer")
public record PrinterProperties(Registry registry, Anthropic anthropic) {

    public record Registry(String url) {}

    /**
     * Spring AI has no application.properties binding for Anthropic's "effort"
     * (reasoning effort for Claude 5+ models) - it's Java-builder-only
     * (AnthropicChatOptions.builder().effort(...)) - so this is our own
     * placeholder property, wired in manually at request time.
     */
    public record Anthropic(String effort) {}
}
