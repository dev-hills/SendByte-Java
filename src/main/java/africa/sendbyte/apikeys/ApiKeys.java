package africa.sendbyte.apikeys;

import africa.sendbyte.http.RequestExecutor;
import africa.sendbyte.http.SendByteHttpRequest;

import java.util.concurrent.CompletableFuture;

/**
 * The API Keys resource: create scoped keys, list active keys, and revoke keys.
 *
 * <p>Obtain an instance via {@code client.apiKeys()}. These operations require a
 * {@code full_access} key. Every method has a blocking form and an {@code ...Async}
 * form returning a {@link CompletableFuture}.
 */
public final class ApiKeys {

    private final RequestExecutor executor;

    public ApiKeys(RequestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Create a new API key. The full key value is returned once, on
     * {@link ApiKey#getKey()} — store it immediately.
     *
     * @param name  a human-readable label, e.g. {@code "Production backend"}
     * @param scope the permission scope
     * @param mode  the environment mode (live or test)
     */
    public ApiKey create(String name, KeyScope scope, KeyMode mode) {
        return executor.execute(buildCreateRequest(name, scope, mode), ApiKey.class);
    }

    /** Asynchronous {@link #create(String, KeyScope, KeyMode)}. */
    public CompletableFuture<ApiKey> createAsync(String name, KeyScope scope, KeyMode mode) {
        return executor.executeAsync(buildCreateRequest(name, scope, mode), ApiKey.class);
    }

    /** List all active (non-revoked) keys. Full key values are not included. */
    public ApiKeyList list() {
        return executor.execute(buildListRequest(), ApiKeyList.class);
    }

    /** Asynchronous {@link #list()}. */
    public CompletableFuture<ApiKeyList> listAsync() {
        return executor.executeAsync(buildListRequest(), ApiKeyList.class);
    }

    /**
     * Revoke a key immediately. A key cannot revoke itself — use a separate
     * {@code full_access} key to perform the revocation.
     */
    public void revoke(String id) {
        executor.execute(buildRevokeRequest(id), Void.class);
    }

    /** Asynchronous {@link #revoke(String)}. */
    public CompletableFuture<Void> revokeAsync(String id) {
        return executor.executeAsync(buildRevokeRequest(id), Void.class);
    }

    private SendByteHttpRequest buildCreateRequest(String name, KeyScope scope, KeyMode mode) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("'name' is required.");
        }
        if (scope == null) {
            throw new IllegalArgumentException("'scope' is required.");
        }
        if (mode == null) {
            throw new IllegalArgumentException("'mode' is required.");
        }
        return SendByteHttpRequest.builder("POST", "/api-keys")
                .body(new CreateApiKeyRequest(name, scope, mode))
                .build();
    }

    private SendByteHttpRequest buildListRequest() {
        return SendByteHttpRequest.builder("GET", "/api-keys").build();
    }

    private SendByteHttpRequest buildRevokeRequest(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("key id must not be null or empty.");
        }
        return SendByteHttpRequest.builder("DELETE", "/api-keys/" + id).build();
    }
}
