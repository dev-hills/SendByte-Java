package africa.sendbyte.emails;

/**
 * The delivery status of an email.
 *
 * <p>Use {@link #fromWire(String)} to parse an API value defensively; unknown or
 * future values map to {@link #UNKNOWN} rather than throwing.
 */
public enum EmailStatus {
    QUEUED("queued"),
    SENT("sent"),
    DELIVERED("delivered"),
    BOUNCED("bounced"),
    COMPLAINED("complained"),
    SUPPRESSED("suppressed"),
    UNKNOWN("unknown");

    private final String wire;

    EmailStatus(String wire) {
        this.wire = wire;
    }

    /** The lowercase string used by the API. */
    public String wire() {
        return wire;
    }

    public static EmailStatus fromWire(String value) {
        if (value != null) {
            for (EmailStatus s : values()) {
                if (s.wire.equalsIgnoreCase(value)) {
                    return s;
                }
            }
        }
        return UNKNOWN;
    }
}
