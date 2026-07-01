package africa.sendbyte.domains;

/** Request body for {@link Domains#create(String)}: {@code { "domain": "..." }}. */
final class CreateDomainRequest {

    private final String domain;

    CreateDomainRequest(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}
