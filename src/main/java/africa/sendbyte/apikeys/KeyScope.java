package africa.sendbyte.apikeys;

/**
 * The permission scope of an API key. Assigned at creation and immutable thereafter.
 *
 * <p>Use {@link #fromWire(String)} to parse defensively; unknown values map to
 * {@link #UNKNOWN} rather than throwing.
 */
public enum KeyScope {
    /** Send emails only. Use for production application servers. */
    SEND_ONLY("send_only"),
    /** Read emails, domains, and logs. Use for dashboards and monitors. */
    READ_ONLY("read_only"),
    /** All operations, including creating and revoking keys. */
    FULL_ACCESS("full_access"),
    UNKNOWN("unknown");

    private final String wire;

    KeyScope(String wire) {
        this.wire = wire;
    }

    public String wire() {
        return wire;
    }

    public static KeyScope fromWire(String value) {
        if (value != null) {
            for (KeyScope s : values()) {
                if (s.wire.equalsIgnoreCase(value)) {
                    return s;
                }
            }
        }
        return UNKNOWN;
    }
}
