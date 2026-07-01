package africa.sendbyte.webhooks;

import java.util.List;

/** The result of {@link Webhooks#list()}: all webhook endpoints on the account. */
public final class WebhookEndpointList {

    private List<WebhookEndpoint> data;

    /** The registered endpoints. Signing secrets are never included here. */
    public List<WebhookEndpoint> getData() {
        return data;
    }

    public void setData(List<WebhookEndpoint> data) {
        this.data = data;
    }
}
