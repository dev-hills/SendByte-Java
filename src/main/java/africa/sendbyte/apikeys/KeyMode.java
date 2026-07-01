package africa.sendbyte.apikeys;

/**
 * The environment mode of an API key.
 *
 * <p>Use {@link #fromWire(String)} to parse defensively; unknown values map to
 * {@link #UNKNOWN} rather than throwing.
 */
public enum KeyMode {
    /** Sends real email. Requires a verified sending domain. Keys are prefixed {@code sk_live_}. */
    LIVE("live"),
    /** Sandbox mode; fully simulated delivery, no domain required. Keys are prefixed {@code sk_test_}. */
    TEST("test"),
    UNKNOWN("unknown");

    private final String wire;

    KeyMode(String wire) {
        this.wire = wire;
    }

    public String wire() {
        return wire;
    }

    public static KeyMode fromWire(String value) {
        if (value != null) {
            for (KeyMode m : values()) {
                if (m.wire.equalsIgnoreCase(value)) {
                    return m;
                }
            }
        }
        return UNKNOWN;
    }
}
