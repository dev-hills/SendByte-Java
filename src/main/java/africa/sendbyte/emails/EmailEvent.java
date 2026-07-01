package africa.sendbyte.emails;

import java.util.Map;

/**
 * A single lifecycle event in an email's timeline, e.g. {@code email.sent} or
 * {@code email.delivered}.
 */
public final class EmailEvent {

    private String type;
    private Map<String, Object> payload;
    private String createdAt;

    /** The event name, e.g. {@code email.delivered}. */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** Event-specific metadata such as SMTP response codes or delivery timing. */
    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    /** ISO 8601 timestamp of when the event was recorded. */
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
