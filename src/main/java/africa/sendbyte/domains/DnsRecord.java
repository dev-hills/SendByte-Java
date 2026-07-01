package africa.sendbyte.domains;

/**
 * A DNS record you must publish to authenticate a sending domain, returned by
 * {@link Domains#create(String)} and {@link Domains#get(String)}.
 */
public final class DnsRecord {

    private String type;
    private String host;
    private String value;
    private String purpose;
    private boolean required;

    /** DNS record type. Always {@code TXT} for the SPF, DKIM, and DMARC records. */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** The host/name to publish the record at, e.g. {@code sb._domainkey.paylink.ng}. */
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /** The exact TXT value to publish. Do not modify it. */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /** The protocol this record serves: {@code spf}, {@code dkim}, or {@code dmarc}. */
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /** {@code true} if this record must be present for the domain to verify (SPF and DKIM). */
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
