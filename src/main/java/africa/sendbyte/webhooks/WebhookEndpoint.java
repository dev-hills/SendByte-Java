package africa.sendbyte.webhooks;

import java.util.List;

/**
 * A registered webhook endpoint that SendByte delivers events to.
 *
 * <p>The {@link #getSecret() secret} is only populated on the {@link Webhooks#create}
 * response — it is shown once and never returned again. Pass it to
 * {@link WebhookSignature} to verify incoming deliveries.
 */
public final class WebhookEndpoint {

    private String id;
    private String url;
    private List<String> events;
    private boolean disabled;
    private String secret;
    private String createdAt;

    /** Unique endpoint identifier (prefixed {@code wh_}). */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The HTTPS destination URL. */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /** The subscribed event types. Empty or {@code null} means all events. */
    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    /** {@code true} if this endpoint subscribes to every event type. */
    public boolean receivesAllEvents() {
        return events == null || events.isEmpty();
    }

    /** {@code true} if the endpoint has been disabled. */
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /** The signing secret ({@code whsec_...}). Present only on the create response. */
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
