package nl.makan1869.capabilitymesh.printer.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class McpUrlsTest {

    @Test
    void stripsTheDefaultMcpEndpointSuffix() {
        assertThat(McpUrls.baseUriOf("http://localhost:8081/mcp")).isEqualTo("http://localhost:8081");
    }

    @Test
    void leavesUrlsWithoutTheSuffixUnchanged() {
        assertThat(McpUrls.baseUriOf("http://localhost:8081")).isEqualTo("http://localhost:8081");
    }
}
