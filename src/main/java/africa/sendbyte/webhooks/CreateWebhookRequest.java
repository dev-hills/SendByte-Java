package africa.sendbyte.webhooks;

import java.util.List;

/**
 * Request body for {@link Webhooks#create}: {@code { "url": "...", "events": [...] }}.
 * A {@code null} events list is omitted, which subscribes the endpoint to all events.
 */
final class CreateWebhookRequest {

    private final String url;
    private final List<String> events;

    CreateWebhookRequest(String url, List<String> events) {
        this.url = url;
        this.events = (events == null || events.isEmpty()) ? null : events;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getEvents() {
        return events;
    }
}
