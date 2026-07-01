package africa.sendbyte.emails;

import africa.sendbyte.http.RequestExecutor;
import africa.sendbyte.http.SendByteHttpRequest;

import java.util.concurrent.CompletableFuture;

/**
 * The Emails resource: send transactional emails and read them back.
 *
 * <p>Obtain an instance via {@code client.emails()}. Every method has a blocking
 * form and an {@code ...Async} form returning a {@link CompletableFuture}.
 */
public final class Emails {

    private final RequestExecutor executor;

    public Emails(RequestExecutor executor) {
        this.executor = executor;
    }

    /**
     * Send a transactional email.
     *
     * @return the created {@link Email} (initially {@code queued}); store {@link Email#getId()}
     *         to look it up later.
     */
    public Email send(SendEmailRequest request) {
        return executor.execute(buildSendRequest(request), Email.class);
    }

    /** Asynchronous {@link #send(SendEmailRequest)}. */
    public CompletableFuture<Email> sendAsync(SendEmailRequest request) {
        return executor.executeAsync(buildSendRequest(request), Email.class);
    }

    /** Retrieve a single email by id, including its rendered body and full event timeline. */
    public Email get(String id) {
        return executor.execute(buildGetRequest(id), Email.class);
    }

    /** Asynchronous {@link #get(String)}. */
    public CompletableFuture<Email> getAsync(String id) {
        return executor.executeAsync(buildGetRequest(id), Email.class);
    }

    /** List sent emails, newest-first, using default paging. */
    public EmailList list() {
        return list(null);
    }

    /** List sent emails with optional filtering and cursor pagination. */
    public EmailList list(ListEmailsParams params) {
        return executor.execute(buildListRequest(params), EmailList.class);
    }

    /** Asynchronous {@link #list(ListEmailsParams)}. */
    public CompletableFuture<EmailList> listAsync(ListEmailsParams params) {
        return executor.executeAsync(buildListRequest(params), EmailList.class);
    }

    private SendByteHttpRequest buildSendRequest(SendEmailRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null.");
        }
        return SendByteHttpRequest.builder("POST", "/emails")
                .body(request)
                .build();
    }

    private SendByteHttpRequest buildGetRequest(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("email id must not be null or empty.");
        }
        return SendByteHttpRequest.builder("GET", "/emails/" + id).build();
    }

    private SendByteHttpRequest buildListRequest(ListEmailsParams params) {
        SendByteHttpRequest.Builder builder = SendByteHttpRequest.builder("GET", "/emails");
        if (params != null) {
            builder.query("limit", params.getLimit())
                    .query("after", params.getAfter())
                    .query("status", params.getStatus() == null ? null : params.getStatus().wire());
        }
        return builder.build();
    }
}
