package africa.sendbyte.domains;

/**
 * Verification status of a sending domain.
 *
 * <p>Use {@link #fromWire(String)} to parse defensively; unknown values map to
 * {@link #UNKNOWN} rather than throwing.
 */
public enum DomainStatus {
    /** Registered but not yet passed a DNS check. */
    PENDING("pending"),
    /** All required records (SPF + DKIM) passed; ready for live sends. */
    VERIFIED("verified"),
    /** Was verified, but a later check found required records missing or invalid. */
    DEGRADED("degraded"),
    UNKNOWN("unknown");

    private final String wire;

    DomainStatus(String wire) {
        this.wire = wire;
    }

    public String wire() {
        return wire;
    }

    public static DomainStatus fromWire(String value) {
        if (value != null) {
            for (DomainStatus s : values()) {
                if (s.wire.equalsIgnoreCase(value)) {
                    return s;
                }
            }
        }
        return UNKNOWN;
    }
}
