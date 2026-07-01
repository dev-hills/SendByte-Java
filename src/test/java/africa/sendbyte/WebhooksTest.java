package africa.sendbyte;

import africa.sendbyte.webhooks.WebhookDelivery;
import africa.sendbyte.webhooks.WebhookDeliveryList;
import africa.sendbyte.webhooks.WebhookEndpoint;
import africa.sendbyte.webhooks.WebhookEndpointList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhooksTest {

    @Test
    void createWithEventsSerializesUrlAndEventsAndReturnsSecret() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"wh_1\",\"url\":\"https://app.ng/hooks\"," +
                        "\"events\":[\"email.delivered\",\"email.bounced\"],\"disabled\":false," +
                        "\"secret\":\"whsec_abc\",\"created_at\":\"2026-06-13T09:00:00Z\"}");
        SendByteClient client = new SendByteClient(transport);

        WebhookEndpoint endpoint = client.webhooks().create("https://app.ng/hooks",
                Arrays.asList("email.delivered", "email.bounced"));

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/webhooks"), transport.lastUrl);
        assertTrue(transport.lastBody.contains("\"url\":\"https://app.ng/hooks\""), transport.lastBody);
        assertTrue(transport.lastBody.contains("\"events\""), transport.lastBody);

        assertEquals("wh_1", endpoint.getId());
        assertEquals("whsec_abc", endpoint.getSecret());
        assertFalse(endpoint.receivesAllEvents());
        assertEquals(2, endpoint.getEvents().size());
    }

    @Test
    void createWithoutEventsOmitsEventsField() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"wh_2\",\"url\":\"https://app.ng/hooks\",\"events\":[],\"disabled\":false}");
        SendByteClient client = new SendByteClient(transport);

        WebhookEndpoint endpoint = client.webhooks().create("https://app.ng/hooks");

        // No events key in the body means "all events".
        assertFalse(transport.lastBody.contains("events"), transport.lastBody);
        assertTrue(endpoint.receivesAllEvents());
    }

    @Test
    void listParsesEndpoints() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"wh_1\",\"url\":\"https://app.ng/hooks\"," +
                        "\"events\":[\"email.delivered\"],\"disabled\":false}]}");
        SendByteClient client = new SendByteClient(transport);

        WebhookEndpointList list = client.webhooks().list();

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/webhooks"), transport.lastUrl);
        assertEquals(1, list.getData().size());
        assertEquals("wh_1", list.getData().get(0).getId());
    }

    @Test
    void disableUsesDeleteVerb() {
        StubTransport transport = new StubTransport(204, "");
        SendByteClient client = new SendByteClient(transport);

        client.webhooks().disable("wh_1");

        assertEquals("DELETE", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/webhooks/wh_1"), transport.lastUrl);
    }

    @Test
    void deliveriesParsesAttempts() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"del_1\",\"event_type\":\"email.delivered\"," +
                        "\"status_code\":200,\"latency_ms\":184,\"succeeded\":true}]}");
        SendByteClient client = new SendByteClient(transport);

        WebhookDeliveryList list = client.webhooks().deliveries("wh_1");

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/webhooks/wh_1/deliveries"), transport.lastUrl);
        WebhookDelivery d = list.getData().get(0);
        assertEquals("email.delivered", d.getEventType());
        assertEquals(Integer.valueOf(200), d.getStatusCode());
        assertEquals(Boolean.TRUE, d.getSucceeded());
    }

    @Test
    void replayPostsToReplayPathAndHandlesNullOutcome() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"del_2\",\"event_type\":\"email.bounced\",\"replay_of\":\"del_1\"," +
                        "\"status_code\":null,\"latency_ms\":null,\"attempted_at\":null,\"succeeded\":null}");
        SendByteClient client = new SendByteClient(transport);

        WebhookDelivery replay = client.webhooks().replay("del_1");

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/webhooks/deliveries/del_1/replay"), transport.lastUrl);
        assertEquals("del_1", replay.getReplayOf());
        assertNull(replay.getStatusCode());
        assertNull(replay.getSucceeded());
    }

    @Test
    void createRejectsBlankUrl() {
        SendByteClient client = new SendByteClient(new StubTransport(200, "{}"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.webhooks().create("  "));
    }
}
