package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Transport configuration for either a local package ({@link Package#transport()}) or a
 * remote server ({@link ServerDetail#remotes()}). The spec models these as two separate
 * types (LocalTransport vs. RemoteTransport = LocalTransport + "variables"), but since
 * "variables" is simply unused when a transport describes a local package, both contexts
 * share the same {@link StreamableHttpTransport}/{@link SseTransport} records here.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StdioTransport.class, name = "stdio"),
    @JsonSubTypes.Type(value = StreamableHttpTransport.class, name = "streamable-http"),
    @JsonSubTypes.Type(value = SseTransport.class, name = "sse")
})
public sealed interface Transport permits StdioTransport, StreamableHttpTransport, SseTransport {}
