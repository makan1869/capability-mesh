package nl.makan1869.capabilitymesh.printer.mcp;

public final class McpUrls {

    /** Matches the default Streamable HTTP endpoint path used by every server in this mesh. */
    private static final String DEFAULT_ENDPOINT_SUFFIX = "/mcp";

    private McpUrls() {}

    /**
     * HttpClientStreamableHttpTransport takes a base URI (scheme+host+port) separately
     * from the endpoint path (which defaults to "/mcp"), but the registry stores the full
     * URL including that path - so strip it back off before building the transport.
     */
    public static String baseUriOf(String mcpUrl) {
        if (mcpUrl.endsWith(DEFAULT_ENDPOINT_SUFFIX)) {
            return mcpUrl.substring(0, mcpUrl.length() - DEFAULT_ENDPOINT_SUFFIX.length());
        }
        return mcpUrl;
    }
}
