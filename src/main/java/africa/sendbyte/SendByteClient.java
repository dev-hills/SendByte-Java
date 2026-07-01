package africa.sendbyte;

import africa.sendbyte.domains.Domains;
import africa.sendbyte.emails.Emails;
import africa.sendbyte.templates.Templates;
import africa.sendbyte.webhooks.Webhooks;
import africa.sendbyte.http.HttpTransport;
import africa.sendbyte.http.OkHttpTransport;
import africa.sendbyte.http.RequestExecutor;

/**
 * The entry point to the SendByte API.
 *
 * <p>Create one client for the lifetime of your application and reuse it; it is
 * thread-safe. Resources are reached through accessor methods:
 *
 * <pre>{@code
 * SendByteClient client = new SendByteClient(System.getenv("SENDBYTE_API_KEY"));
 *
 * Email email = client.emails().send(
 *     SendEmailRequest.builder()
 *         .from("PayLink <receipts@paylink.ng>")
 *         .to("amaka@halo.ng")
 *         .subject("Receipt for ₦45,000")
 *         .html("<p>Payment received.</p>")
 *         .build());
 *
 * System.out.println(email.getId());
 * }</pre>
 *
 * <p>Use a {@code sk_test_} key while developing and a {@code sk_live_} key in
 * production. Call {@link #close()} on shutdown to release the HTTP connection pool.
 */
public final class SendByteClient implements AutoCloseable {

    private final HttpTransport transport;
    private final Emails emails;
    private final Domains domains;
    private final Templates templates;
    private final Webhooks webhooks;

    /** Create a client with the given API key and default configuration. */
    public SendByteClient(String apiKey) {
        this(SendByteClientOptions.builder().apiKey(apiKey).build());
    }

    /** Create a client from a full options object. */
    public SendByteClient(SendByteClientOptions options) {
        this(new OkHttpTransport(options));
    }

    /**
     * Create a client over a custom {@link HttpTransport}. Primarily used by tests to
     * inject a stub transport, but also allows swapping the HTTP layer entirely.
     */
    public SendByteClient(HttpTransport transport) {
        this.transport = transport;
        RequestExecutor executor = new RequestExecutor(transport);
        this.emails = new Emails(executor);
        this.domains = new Domains(executor);
        this.templates = new Templates(executor);
        this.webhooks = new Webhooks(executor);
    }

    /** Access the Emails resource: send, retrieve, and list transactional emails. */
    public Emails emails() {
        return emails;
    }

    /** Access the Domains resource: register, retrieve, list, and verify sending domains. */
    public Domains domains() {
        return domains;
    }

    /** Access the Templates resource: manage and preview reusable email templates. */
    public Templates templates() {
        return templates;
    }

    /** Access the Webhooks resource: manage endpoints, inspect deliveries, and replay them. */
    public Webhooks webhooks() {
        return webhooks;
    }

    @Override
    public void close() {
        transport.close();
    }
}
