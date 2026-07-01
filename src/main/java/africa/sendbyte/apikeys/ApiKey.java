package africa.sendbyte.apikeys;

/**
 * An API key.
 *
 * <p>The full {@link #getKey() key} is populated only on the {@link ApiKeys#create}
 * response and is shown once. List responses instead expose a masked {@link #getPrefix()
 * prefix}.
 */
public final class ApiKey {

    private String id;
    private String name;
    private String key;
    private String prefix;
    private String scope;
    private String mode;
    private String lastUsedAt;
    private String createdAt;

    /** Unique key identifier (prefixed {@code key_}); pass to {@link ApiKeys#revoke(String)}. */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The human-readable label assigned at creation. */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** The full API key string. Present only on the create response — store it immediately. */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /** A masked prefix identifying the key (e.g. {@code sk_live_84f8e388...}). Present on list. */
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /** Raw scope string. See {@link #scope()} for the typed value. */
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /** The scope as a typed enum, mapping unknown values to {@link KeyScope#UNKNOWN}. */
    public KeyScope scope() {
        return KeyScope.fromWire(scope);
    }

    /** Raw mode string. See {@link #mode()} for the typed value. */
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /** The mode as a typed enum, mapping unknown values to {@link KeyMode#UNKNOWN}. */
    public KeyMode mode() {
        return KeyMode.fromWire(mode);
    }

    /** ISO 8601 timestamp of the most recent authenticated request, or {@code null} if unused. */
    public String getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(String lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
