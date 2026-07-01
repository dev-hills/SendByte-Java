package africa.sendbyte.http;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Abstraction over the underlying HTTP client.
 *
 * <p>The default implementation is {@link OkHttpTransport}. Tests inject a stub
 * so no network calls are made. Implementations are responsible for applying the
 * base URL, the {@code Authorization} bearer header, and JSON content headers.
 *
 * <p>Internal SDK type.
 */
public interface HttpTransport {

    /** Execute a request synchronously. */
    SendByteResponse execute(String method,
                             String url,
                             java.util.Map<String, String> headers,
                             String jsonBody) throws IOException;

    /** Execute a request asynchronously. */
    CompletableFuture<SendByteResponse> executeAsync(String method,
                                                     String url,
                                                     java.util.Map<String, String> headers,
                                                     String jsonBody);

    /** The base URL (including scheme and version prefix) requests are sent to. */
    String baseUrl();

    /** Release any underlying resources. Optional. */
    default void close() {
    }
}
