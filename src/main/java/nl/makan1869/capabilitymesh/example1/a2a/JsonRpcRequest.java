package nl.makan1869.capabilitymesh.example1.a2a;

/**
 * JSON-RPC 2.0 request envelope used by the A2A protocol.
 */
public record JsonRpcRequest<P>(
        String jsonrpc,
        String id,
        String method,
        P params
) {
    public static <P> JsonRpcRequest<P> of(String id, String method, P params) {
        return new JsonRpcRequest<>("2.0", id, method, params);
    }
}
