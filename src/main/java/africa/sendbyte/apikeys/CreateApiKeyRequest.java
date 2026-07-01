package africa.sendbyte.apikeys;

/** Request body for {@link ApiKeys#create}: {@code { "name": "...", "scope": "...", "mode": "..." }}. */
final class CreateApiKeyRequest {

    private final String name;
    private final String scope;
    private final String mode;

    CreateApiKeyRequest(String name, KeyScope scope, KeyMode mode) {
        this.name = name;
        this.scope = scope == null ? null : scope.wire();
        this.mode = mode == null ? null : mode.wire();
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public String getMode() {
        return mode;
    }
}
