package nl.makan1869.capabilitymesh.example1.a2a;

/**
 * JSON-RPC 2.0 response envelope used by the A2A protocol.
 */
public record JsonRpcResponse<R>(
        String jsonrpc,
        String id,
        R result
) {}
