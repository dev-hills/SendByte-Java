package africa.sendbyte.domains;

/**
 * The result of a live DNS check for a single record, returned by
 * {@link Domains#verify(String)}.
 */
public final class DomainCheck {

    private String purpose;
    private String host;
    private boolean required;
    private boolean pass;

    /** The protocol this check covers: {@code spf}, {@code dkim}, or {@code dmarc}. */
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /** The DNS host that was queried. */
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /** {@code true} if this record must pass for the domain to be marked verified. */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /** {@code true} if the expected record was found and valid at query time. */
    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
