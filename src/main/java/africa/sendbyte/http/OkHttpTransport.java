package africa.sendbyte.http;

import africa.sendbyte.SendByteClientOptions;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Default {@link HttpTransport} backed by OkHttp.
 *
 * <p>Applies the base URL, bearer authentication, JSON content headers, and a
 * SDK User-Agent to every request. Sync uses OkHttp's blocking call; async uses
 * OkHttp's native {@link Call#enqueue(Callback)} dispatcher.
 */
public final class OkHttpTransport implements HttpTransport {

    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiKey;
    private final String baseUrl;
    private final String userAgent;

    public OkHttpTransport(SendByteClientOptions options) {
        this.apiKey = options.apiKey();
        this.baseUrl = options.baseUrl();
        this.userAgent = options.userAgent();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(options.connectTimeout())
                .readTimeout(options.readTimeout())
                .writeTimeout(options.writeTimeout())
                .build();
    }

    @Override
    public String baseUrl() {
        return baseUrl;
    }

    @Override
    public SendByteResponse execute(String method, String url, Map<String, String> headers, String jsonBody)
            throws IOException {
        try (Response response = client.newCall(buildRequest(method, url, headers, jsonBody)).execute()) {
            return toSendByteResponse(response);
        }
    }

    @Override
    public CompletableFuture<SendByteResponse> executeAsync(String method, String url,
                                                            Map<String, String> headers, String jsonBody) {
        CompletableFuture<SendByteResponse> future = new CompletableFuture<>();
        client.newCall(buildRequest(method, url, headers, jsonBody)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (Response r = response) {
                    future.complete(toSendByteResponse(r));
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    private Request buildRequest(String method, String url, Map<String, String> headers, String jsonBody) {
        RequestBody requestBody = null;
        if (jsonBody != null) {
            requestBody = RequestBody.create(jsonBody, JSON_MEDIA);
        } else if (requiresBody(method)) {
            // POST/PUT with no payload still need an (empty) body in OkHttp.
            requestBody = RequestBody.create("", JSON_MEDIA);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, requestBody)
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .header("User-Agent", userAgent);

        if (jsonBody != null) {
            builder.header("Content-Type", "application/json; charset=utf-8");
        }
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                builder.header(e.getKey(), e.getValue());
            }
        }
        return builder.build();
    }

    private static boolean requiresBody(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }

    private static SendByteResponse toSendByteResponse(Response response) throws IOException {
        String body = response.body() != null ? response.body().string() : "";
        final Headers h = response.headers();
        return new SendByteResponse(response.code(), body, h::get);
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}
