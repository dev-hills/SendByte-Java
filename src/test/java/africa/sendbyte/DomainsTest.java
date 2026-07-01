package africa.sendbyte;

import africa.sendbyte.domains.Domain;
import africa.sendbyte.domains.DomainList;
import africa.sendbyte.domains.DomainStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainsTest {

    @Test
    void createSerializesDomainAndParsesDnsRecords() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"dom_1\",\"domain\":\"paylink.ng\",\"status\":\"pending\"," +
                        "\"dkim_selector\":\"sb\",\"verified_at\":null," +
                        "\"dns_records\":[" +
                        "{\"type\":\"TXT\",\"host\":\"paylink.ng\",\"value\":\"v=spf1 ...\"," +
                        "\"purpose\":\"spf\",\"required\":true}," +
                        "{\"type\":\"TXT\",\"host\":\"sb._domainkey.paylink.ng\",\"value\":\"v=DKIM1; ...\"," +
                        "\"purpose\":\"dkim\",\"required\":true}]}");
        SendByteClient client = new SendByteClient(transport);

        Domain domain = client.domains().create("paylink.ng");

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/domains"), transport.lastUrl);
        assertEquals("{\"domain\":\"paylink.ng\"}", transport.lastBody);

        assertEquals("dom_1", domain.getId());
        assertEquals(DomainStatus.PENDING, domain.status());
        assertEquals("sb", domain.getDkimSelector());
        assertNull(domain.getVerifiedAt());
        assertEquals(2, domain.getDnsRecords().size());
        assertEquals("dkim", domain.getDnsRecords().get(1).getPurpose());
        assertTrue(domain.getDnsRecords().get(1).isRequired());
    }

    @Test
    void verifyPostsToVerifyPathAndParsesChecks() {
        StubTransport transport = new StubTransport(200,
                "{\"id\":\"dom_1\",\"domain\":\"paylink.ng\",\"status\":\"verified\"," +
                        "\"checks\":[" +
                        "{\"purpose\":\"spf\",\"host\":\"paylink.ng\",\"required\":true,\"pass\":true}," +
                        "{\"purpose\":\"dmarc\",\"host\":\"_dmarc.paylink.ng\",\"required\":false,\"pass\":false}]}");
        SendByteClient client = new SendByteClient(transport);

        Domain domain = client.domains().verify("dom_1");

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/domains/dom_1/verify"), transport.lastUrl);
        assertEquals(DomainStatus.VERIFIED, domain.status());
        assertEquals(2, domain.getChecks().size());
        assertTrue(domain.getChecks().get(0).isPass());
        assertFalse(domain.getChecks().get(1).isRequired());
    }

    @Test
    void listParsesData() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"dom_1\",\"domain\":\"paylink.ng\",\"status\":\"verified\"}]}");
        SendByteClient client = new SendByteClient(transport);

        DomainList list = client.domains().list();

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/domains"), transport.lastUrl);
        assertEquals(1, list.getData().size());
        assertEquals("paylink.ng", list.getData().get(0).getDomain());
    }

    @Test
    void getFetchesById() {
        StubTransport transport = new StubTransport(200,
                "{\"id\":\"dom_9\",\"domain\":\"mail.paylink.ng\",\"status\":\"degraded\"}");
        SendByteClient client = new SendByteClient(transport);

        Domain domain = client.domains().get("dom_9");

        assertTrue(transport.lastUrl.endsWith("/domains/dom_9"), transport.lastUrl);
        assertEquals(DomainStatus.DEGRADED, domain.status());
    }

    @Test
    void createRejectsBlankDomain() {
        SendByteClient client = new SendByteClient(new StubTransport(200, "{}"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> client.domains().create("  "));
    }
}
