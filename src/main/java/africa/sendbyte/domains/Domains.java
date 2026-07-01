package africa.sendbyte.domains;

import africa.sendbyte.http.RequestExecutor;
import africa.sendbyte.http.SendByteHttpRequest;

import java.util.concurrent.CompletableFuture;

/**
 * The Domains resource: register sending domains, fetch their DNS records, and
 * trigger verification.
 *
 * <p>Obtain an instance via {@code client.domains()}. Every method has a blocking
 * form and an {@code ...Async} form returning a {@link CompletableFuture}.
 */
public final class Domains {

    private final RequestExecutor executor;

    public Domains(RequestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Register a sending domain and receive the SPF, DKIM, and DMARC records to publish.
     * Re-registering an existing domain returns the existing record (idempotent).
     *
     * @param domain the bare domain name, e.g. {@code paylink.ng} or {@code mail.paylink.ng}
     */
    public Domain create(String domain) {
        return executor.execute(buildCreateRequest(domain), Domain.class);
    }

    /** Asynchronous {@link #create(String)}. */
    public CompletableFuture<Domain> createAsync(String domain) {
        return executor.executeAsync(buildCreateRequest(domain), Domain.class);
    }

    /** Fetch a single domain and its verification status by id. */
    public Domain get(String id) {
        return executor.execute(buildGetRequest(id), Domain.class);
    }

    /** Asynchronous {@link #get(String)}. */
    public CompletableFuture<Domain> getAsync(String id) {
        return executor.executeAsync(buildGetRequest(id), Domain.class);
    }

    /** List all domains registered on the account. */
    public DomainList list() {
        return executor.execute(buildListRequest(), DomainList.class);
    }

    /** Asynchronous {@link #list()}. */
    public CompletableFuture<DomainList> listAsync() {
        return executor.executeAsync(buildListRequest(), DomainList.class);
    }

    /**
     * Trigger a live DNS check. When all required records (SPF + DKIM) pass, the
     * returned domain's status flips to {@link DomainStatus#VERIFIED}.
     */
    public Domain verify(String id) {
        return executor.execute(buildVerifyRequest(id), Domain.class);
    }

    /** Asynchronous {@link #verify(String)}. */
    public CompletableFuture<Domain> verifyAsync(String id) {
        return executor.executeAsync(buildVerifyRequest(id), Domain.class);
    }

    private SendByteHttpRequest buildCreateRequest(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("domain must not be null or empty.");
        }
        return SendByteHttpRequest.builder("POST", "/domains")
                .body(new CreateDomainRequest(domain))
                .build();
    }

    private SendByteHttpRequest buildGetRequest(String id) {
        return SendByteHttpRequest.builder("GET", "/domains/" + requireId(id)).build();
    }

    private SendByteHttpRequest buildListRequest() {
        return SendByteHttpRequest.builder("GET", "/domains").build();
    }

    private SendByteHttpRequest buildVerifyRequest(String id) {
        return SendByteHttpRequest.builder("POST", "/domains/" + requireId(id) + "/verify").build();
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("domain id must not be null or empty.");
        }
        return id;
    }
}
