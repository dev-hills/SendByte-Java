package africa.sendbyte;

import africa.sendbyte.http.HttpTransport;
import africa.sendbyte.http.SendByteResponse;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * A test double for {@link HttpTransport} that returns a preconfigured response and
 * records the request it received. No network calls are made.
 */
final class StubTransport implements HttpTransport {

    private final int statusCode;
    private final String responseBody;
    private final Map<String, String> responseHeaders;

    // Captured request details.
    String lastMethod;
    String lastUrl;
    Map<String, String> lastHeaders;
    String lastBody;

    StubTransport(int statusCode, String responseBody) {
        this(statusCode, responseBody, new TreeMap<>());
    }

    StubTransport(int statusCode, String responseBody, Map<String, String> responseHeaders) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        // Case-insensitive header storage to mirror real HTTP behaviour.
        this.responseHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.responseHeaders.putAll(responseHeaders);
    }

    private void capture(String method, String url, Map<String, String> headers, String body) {
        this.lastMethod = method;
        this.lastUrl = url;
        this.lastHeaders = headers;
        this.lastBody = body;
    }

    private SendByteResponse response() {
        return new SendByteResponse(statusCode, responseBody, responseHeaders::get);
    }

    @Override
    public SendByteResponse execute(String method, String url, Map<String, String> headers, String jsonBody) {
        capture(method, url, headers, jsonBody);
        return response();
    }

    @Override
    public CompletableFuture<SendByteResponse> executeAsync(String method, String url,
                                                            Map<String, String> headers, String jsonBody) {
        capture(method, url, headers, jsonBody);
        return CompletableFuture.completedFuture(response());
    }

    @Override
    public String baseUrl() {
        return "https://api.test/v1";
    }
}
