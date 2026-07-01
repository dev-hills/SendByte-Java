package africa.sendbyte.http;

import africa.sendbyte.exceptions.ApiErrorEnvelope;
import africa.sendbyte.exceptions.SendByteConnectionException;
import africa.sendbyte.exceptions.SendByteException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Central request pipeline: builds the URL, serializes the body, delegates to the
 * {@link HttpTransport}, and turns the raw response into either a typed model or a
 * {@link SendByteException}.
 *
 * <p>Internal SDK type shared by all resource classes.
 */
public final class RequestExecutor {

    private final HttpTransport transport;
    private final Json json;

    public RequestExecutor(HttpTransport transport) {
        this.transport = transport;
        this.json = new Json();
    }

    public Json json() {
        return json;
    }

    /** Execute a request and deserialize the success body into {@code type}. */
    public <T> T execute(SendByteHttpRequest request, Class<T> type) {
        String url = buildUrl(request);
        String body = request.body() == null ? null : json.write(request.body());
        SendByteResponse response;
        try {
            response = transport.execute(request.method(), url, request.headers(), body);
        } catch (IOException e) {
            throw new SendByteConnectionException("Failed to reach the SendByte API: " + e.getMessage(), e);
        }
        return handle(response, type);
    }

    /** Execute a request asynchronously and deserialize the success body into {@code type}. */
    public <T> CompletableFuture<T> executeAsync(SendByteHttpRequest request, Class<T> type) {
        String url = buildUrl(request);
        String body = request.body() == null ? null : json.write(request.body());
        return transport.executeAsync(request.method(), url, request.headers(), body)
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        Throwable cause = throwable instanceof CompletionException && throwable.getCause() != null
                                ? throwable.getCause() : throwable;
                        throw new SendByteConnectionException(
                                "Failed to reach the SendByte API: " + cause.getMessage(), cause);
                    }
                    return handle(response, type);
                });
    }

    private <T> T handle(SendByteResponse response, Class<T> type) {
        if (response.isSuccessful()) {
            if (type == Void.class || response.body() == null || response.body().isEmpty()) {
                return null;
            }
            return json.read(response.body(), type);
        }
        throw toException(response);
    }

    private SendByteException toException(SendByteResponse response) {
        String code = null;
        String message = "Request failed with HTTP " + response.statusCode();
        String docsUrl = null;

        String rawBody = response.body();
        if (rawBody != null && !rawBody.isEmpty()) {
            try {
                ApiErrorEnvelope envelope = json.read(rawBody, ApiErrorEnvelope.class);
                if (envelope != null && envelope.getError() != null) {
                    ApiErrorEnvelope.ApiError err = envelope.getError();
                    code = err.getCode();
                    if (err.getMessage() != null) {
                        message = err.getMessage();
                    }
                    docsUrl = err.getDocsUrl();
                }
            } catch (RuntimeException ignored) {
                // Non-JSON or unexpected error body; fall back to the generic message.
            }
        }

        String requestId = response.header("x-request-id");
        Integer retryAfter = parseRetryAfter(response.header("Retry-After"));
        return SendByteException.fromApiError(response.statusCode(), code, message, docsUrl, requestId, retryAfter);
    }

    private static Integer parseRetryAfter(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String buildUrl(SendByteHttpRequest request) {
        StringBuilder url = new StringBuilder(transport.baseUrl());
        // baseUrl has no trailing slash; path starts with "/".
        url.append(request.path());
        Map<String, String> query = request.query();
        if (query != null && !query.isEmpty()) {
            url.append('?');
            boolean first = true;
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (!first) {
                    url.append('&');
                }
                url.append(encode(entry.getKey())).append('=').append(encode(entry.getValue()));
                first = false;
            }
        }
        return url.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }
}
