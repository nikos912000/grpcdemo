import java.util.Map;

import io.grpc.Metadata;

/**
 * Util class for gRPC related functionality.
 */
public class GrpcUtils {
    static Metadata getMetadata(Map<String, String> headers) {
        Metadata metadata = new Metadata();
        headers.forEach(((k,v) -> {
            Metadata.Key key = Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER);
            metadata.put(key, v);

        }));
        return metadata;
    }
}
