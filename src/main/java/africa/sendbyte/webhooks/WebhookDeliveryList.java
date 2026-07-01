package africa.sendbyte.webhooks;

import java.util.List;

/** The result of {@link Webhooks#deliveries(String)}: recent delivery attempts, newest first. */
public final class WebhookDeliveryList {

    private List<WebhookDelivery> data;

    /** The delivery attempts (up to the last 50), newest first. */
    public List<WebhookDelivery> getData() {
        return data;
    }

    public void setData(List<WebhookDelivery> data) {
        this.data = data;
    }
}
