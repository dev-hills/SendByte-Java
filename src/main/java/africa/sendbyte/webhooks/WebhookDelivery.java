package africa.sendbyte.webhooks;

/**
 * A single webhook delivery attempt, from {@link Webhooks#deliveries(String)} or the
 * fresh attempt created by {@link Webhooks#replay(String)}.
 *
 * <p>On a just-created replay, the outcome fields ({@code statusCode}, {@code latencyMs},
 * {@code attemptedAt}, {@code succeeded}) are {@code null} until the attempt runs.
 */
public final class WebhookDelivery {

    private String id;
    private String eventType;
    private Integer statusCode;
    private Integer latencyMs;
    private String attemptedAt;
    private Boolean succeeded;
    private String replayOf;

    /** Unique delivery identifier (prefixed {@code del_}); pass to {@link Webhooks#replay(String)}. */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The event type that triggered this delivery, e.g. {@code email.bounced}. */
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /** HTTP status your endpoint returned; {@code 0} if the connection failed, {@code null} if not yet attempted. */
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /** Round-trip latency in milliseconds, or {@code null} if not yet attempted. */
    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    /** ISO 8601 timestamp of the attempt, or {@code null} if not yet attempted. */
    public String getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(String attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    /** {@code true} if your endpoint returned 2xx in time; {@code null} if not yet attempted. */
    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
    }

    /** For a replay, the id of the original delivery it was created from; {@code null} otherwise. */
    public String getReplayOf() {
        return replayOf;
    }

    public void setReplayOf(String replayOf) {
        this.replayOf = replayOf;
    }
}
