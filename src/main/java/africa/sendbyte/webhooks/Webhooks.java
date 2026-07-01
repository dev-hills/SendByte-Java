package africa.sendbyte.webhooks;

import africa.sendbyte.http.RequestExecutor;
import africa.sendbyte.http.SendByteHttpRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The Webhooks resource: register and manage webhook endpoints, inspect delivery
 * history, and replay deliveries.
 *
 * <p>Obtain an instance via {@code client.webhooks()}. Every method has a blocking form
 * and an {@code ...Async} form returning a {@link CompletableFuture}. To verify the
 * signature on incoming deliveries, use {@link WebhookSignature}.
 */
public final class Webhooks {

    private final RequestExecutor executor;

    public Webhooks(RequestExecutor executor) {
        this.executor = executor;
    }

    /** Register an endpoint subscribed to all event types. */
    public WebhookEndpoint create(String url) {
        return create(url, null);
    }

    /**
     * Register an endpoint subscribed to specific event types (e.g.
     * {@code ["email.delivered", "email.bounced"]}). Pass {@code null} or an empty list
     * to receive all events. The response includes the one-time signing secret.
     */
    public WebhookEndpoint create(String url, List<String> events) {
        return executor.execute(buildCreateRequest(url, events), WebhookEndpoint.class);
    }

    /** Asynchronous {@link #create(String, List)}. */
    public CompletableFuture<WebhookEndpoint> createAsync(String url, List<String> events) {
        return executor.executeAsync(buildCreateRequest(url, events), WebhookEndpoint.class);
    }

    /** List all webhook endpoints on the account. Signing secrets are not included. */
    public WebhookEndpointList list() {
        return executor.execute(buildListRequest(), WebhookEndpointList.class);
    }

    /** Asynchronous {@link #list()}. */
    public CompletableFuture<WebhookEndpointList> listAsync() {
        return executor.executeAsync(buildListRequest(), WebhookEndpointList.class);
    }

    /** Disable an endpoint so it stops receiving deliveries. Delivery history is retained. */
    public void disable(String id) {
        executor.execute(buildDisableRequest(id), Void.class);
    }

    /** Asynchronous {@link #disable(String)}. */
    public CompletableFuture<Void> disableAsync(String id) {
        return executor.executeAsync(buildDisableRequest(id), Void.class);
    }

    /** Fetch the recent delivery attempts (up to the last 50) for an endpoint. */
    public WebhookDeliveryList deliveries(String endpointId) {
        return executor.execute(buildDeliveriesRequest(endpointId), WebhookDeliveryList.class);
    }

    /** Asynchronous {@link #deliveries(String)}. */
    public CompletableFuture<WebhookDeliveryList> deliveriesAsync(String endpointId) {
        return executor.executeAsync(buildDeliveriesRequest(endpointId), WebhookDeliveryList.class);
    }

    /** Re-enqueue a past delivery as a fresh attempt. Returns the new delivery record. */
    public WebhookDelivery replay(String deliveryId) {
        return executor.execute(buildReplayRequest(deliveryId), WebhookDelivery.class);
    }

    /** Asynchronous {@link #replay(String)}. */
    public CompletableFuture<WebhookDelivery> replayAsync(String deliveryId) {
        return executor.executeAsync(buildReplayRequest(deliveryId), WebhookDelivery.class);
    }

    private SendByteHttpRequest buildCreateRequest(String url, List<String> events) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("url must not be null or empty.");
        }
        return SendByteHttpRequest.builder("POST", "/webhooks")
                .body(new CreateWebhookRequest(url, events))
                .build();
    }

    private SendByteHttpRequest buildListRequest() {
        return SendByteHttpRequest.builder("GET", "/webhooks").build();
    }

    private SendByteHttpRequest buildDisableRequest(String id) {
        return SendByteHttpRequest.builder("DELETE", "/webhooks/" + requireId(id, "endpoint id")).build();
    }

    private SendByteHttpRequest buildDeliveriesRequest(String endpointId) {
        return SendByteHttpRequest.builder("GET",
                "/webhooks/" + requireId(endpointId, "endpoint id") + "/deliveries").build();
    }

    private SendByteHttpRequest buildReplayRequest(String deliveryId) {
        return SendByteHttpRequest.builder("POST",
                "/webhooks/deliveries/" + requireId(deliveryId, "delivery id") + "/replay").build();
    }

    private static String requireId(String id, String label) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " must not be null or empty.");
        }
        return id;
    }
}
