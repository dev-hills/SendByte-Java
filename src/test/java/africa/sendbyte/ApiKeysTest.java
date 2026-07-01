package africa.sendbyte;

import africa.sendbyte.apikeys.ApiKey;
import africa.sendbyte.apikeys.ApiKeyList;
import africa.sendbyte.apikeys.KeyMode;
import africa.sendbyte.apikeys.KeyScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiKeysTest {

    @Test
    void createSerializesEnumsToWireValuesAndReturnsFullKey() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"key_1\",\"name\":\"Production backend\"," +
                        "\"key\":\"sk_live_84f8e388...\",\"scope\":\"send_only\",\"mode\":\"live\"," +
                        "\"created_at\":\"2026-06-13T10:00:00Z\"}");
        SendByteClient client = new SendByteClient(transport);

        ApiKey key = client.apiKeys().create("Production backend", KeyScope.SEND_ONLY, KeyMode.LIVE);

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/api-keys"), transport.lastUrl);
        String body = transport.lastBody;
        assertTrue(body.contains("\"name\":\"Production backend\""), body);
        assertTrue(body.contains("\"scope\":\"send_only\""), body);
        assertTrue(body.contains("\"mode\":\"live\""), body);

        assertEquals("sk_live_84f8e388...", key.getKey());
        assertEquals(KeyScope.SEND_ONLY, key.scope());
        assertEquals(KeyMode.LIVE, key.mode());
    }

    @Test
    void listParsesMaskedKeys() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"key_1\",\"name\":\"Prod\",\"prefix\":\"sk_live_84f8e388...\"," +
                        "\"scope\":\"full_access\",\"mode\":\"live\",\"last_used_at\":null}]}");
        SendByteClient client = new SendByteClient(transport);

        ApiKeyList list = client.apiKeys().list();

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/api-keys"), transport.lastUrl);
        ApiKey key = list.getData().get(0);
        assertEquals("sk_live_84f8e388...", key.getPrefix());
        assertNull(key.getKey());
        assertNull(key.getLastUsedAt());
        assertEquals(KeyScope.FULL_ACCESS, key.scope());
    }

    @Test
    void revokeUsesDeleteVerb() {
        StubTransport transport = new StubTransport(204, "");
        SendByteClient client = new SendByteClient(transport);

        client.apiKeys().revoke("key_1");

        assertEquals("DELETE", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/api-keys/key_1"), transport.lastUrl);
    }

    @Test
    void createValidatesRequiredArguments() {
        SendByteClient client = new SendByteClient(new StubTransport(200, "{}"));
        assertThrows(IllegalArgumentException.class,
                () -> client.apiKeys().create("  ", KeyScope.SEND_ONLY, KeyMode.TEST));
        assertThrows(IllegalArgumentException.class,
                () -> client.apiKeys().create("name", null, KeyMode.TEST));
        assertThrows(IllegalArgumentException.class,
                () -> client.apiKeys().create("name", KeyScope.SEND_ONLY, null));
    }
}
