package africa.sendbyte.domains;

import java.util.List;

/**
 * A sending domain.
 *
 * <p>Which fields are populated depends on the call: {@link Domains#create(String)}
 * and {@link Domains#get(String)} include {@code dnsRecords}; {@link Domains#verify(String)}
 * includes {@code checks}.
 */
public final class Domain {

    private String id;
    private String domain;
    private String status;
    private String dkimSelector;
    private List<DnsRecord> dnsRecords;
    private List<DomainCheck> checks;
    private String verifiedAt;
    private String createdAt;

    /** Unique domain identifier; use as the {@code id} for {@link Domains#verify(String)}. */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The bare domain name, e.g. {@code paylink.ng}. */
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    /** Raw status string. See {@link #status()} for the typed value. */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** The status as a typed enum, mapping unknown values to {@link DomainStatus#UNKNOWN}. */
    public DomainStatus status() {
        return DomainStatus.fromWire(status);
    }

    /** The DKIM selector (e.g. {@code sb}), when returned. */
    public String getDkimSelector() {
        return dkimSelector;
    }

    public void setDkimSelector(String dkimSelector) {
        this.dkimSelector = dkimSelector;
    }

    /** DNS records to publish. Present on create/get; {@code null} on verify. */
    public List<DnsRecord> getDnsRecords() {
        return dnsRecords;
    }

    public void setDnsRecords(List<DnsRecord> dnsRecords) {
        this.dnsRecords = dnsRecords;
    }

    /** Per-record DNS check results. Present on verify; {@code null} otherwise. */
    public List<DomainCheck> getChecks() {
        return checks;
    }

    public void setChecks(List<DomainCheck> checks) {
        this.checks = checks;
    }

    /** ISO 8601 timestamp of verification, or {@code null} if not yet verified. */
    public String getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(String verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
