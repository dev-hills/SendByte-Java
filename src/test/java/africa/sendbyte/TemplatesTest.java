package africa.sendbyte;

import africa.sendbyte.templates.RenderRequest;
import africa.sendbyte.templates.RenderedTemplate;
import africa.sendbyte.templates.Template;
import africa.sendbyte.templates.TemplateList;
import africa.sendbyte.templates.TemplateRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplatesTest {

    @Test
    void createSerializesFieldsAndParsesResponse() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"tpl_1\",\"name\":\"welcome\",\"subject\":\"Hi {{first_name}}\"," +
                        "\"version\":1,\"format\":\"html\",\"created_at\":\"2026-06-27T09:14:07Z\"}");
        SendByteClient client = new SendByteClient(transport);

        Template tpl = client.templates().create(TemplateRequest.builder()
                .name("welcome")
                .subject("Hi {{first_name}}")
                .html("<p>Hi {{first_name}}.</p>")
                .text("Hi {{first_name}}.")
                .build());

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates"), transport.lastUrl);
        String body = transport.lastBody;
        assertTrue(body.contains("\"name\":\"welcome\""), body);
        assertTrue(body.contains("\"subject\""), body);
        assertTrue(body.contains("\"html\""), body);

        assertEquals("tpl_1", tpl.getId());
        assertEquals(Integer.valueOf(1), tpl.getVersion());
        assertEquals("html", tpl.getFormat());
    }

    @Test
    void updateUsesPutToIdPath() {
        StubTransport transport = new StubTransport(200,
                "{\"id\":\"tpl_1\",\"name\":\"welcome\",\"subject\":\"Hi\",\"version\":2}");
        SendByteClient client = new SendByteClient(transport);

        Template tpl = client.templates().update("tpl_1", TemplateRequest.builder()
                .name("welcome").subject("Hi").html("<p>Hi.</p>").build());

        assertEquals("PUT", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates/tpl_1"), transport.lastUrl);
        assertEquals(Integer.valueOf(2), tpl.getVersion());
    }

    @Test
    void deleteUsesDeleteVerbAndReturnsNoBody() {
        StubTransport transport = new StubTransport(204, "");
        SendByteClient client = new SendByteClient(transport);

        client.templates().delete("tpl_1");

        assertEquals("DELETE", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates/tpl_1"), transport.lastUrl);
    }

    @Test
    void listParsesData() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"tpl_1\",\"name\":\"welcome\"}],\"has_more\":false}");
        SendByteClient client = new SendByteClient(transport);

        TemplateList list = client.templates().list();

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates"), transport.lastUrl);
        assertEquals(1, list.getData().size());
        assertEquals("welcome", list.getData().get(0).getName());
    }

    @Test
    void renderPostsToRenderPathAndParsesOutput() {
        StubTransport transport = new StubTransport(200,
                "{\"subject\":\"Welcome, Amaka!\",\"html\":\"<p>Hi Amaka.</p>\",\"text\":null}");
        SendByteClient client = new SendByteClient(transport);

        RenderedTemplate out = client.templates().render(RenderRequest.builder()
                .subject("Welcome, {{first_name}}!")
                .html("<p>Hi {{first_name}}.</p>")
                .variable("first_name", "Amaka")
                .build());

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates/render"), transport.lastUrl);
        assertTrue(transport.lastBody.contains("\"variables\""), transport.lastBody);
        assertEquals("Welcome, Amaka!", out.getSubject());
    }

    @Test
    void previewPostsVariablesToPreviewPath() {
        StubTransport transport = new StubTransport(200,
                "{\"subject\":\"Welcome, Amaka!\",\"html\":\"<p>Hi Amaka.</p>\"}");
        SendByteClient client = new SendByteClient(transport);

        RenderedTemplate out = client.templates().preview("tpl_1",
                Collections.singletonMap("first_name", "Amaka"));

        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/templates/tpl_1/preview"), transport.lastUrl);
        assertTrue(transport.lastBody.contains("\"variables\""), transport.lastBody);
        assertTrue(transport.lastBody.contains("\"first_name\":\"Amaka\""), transport.lastBody);
        assertEquals("Welcome, Amaka!", out.getSubject());
    }

    @Test
    void createRequestRequiresNameSubjectAndBody() {
        assertThrows(IllegalArgumentException.class,
                () -> TemplateRequest.builder().subject("s").html("h").build());
        assertThrows(IllegalArgumentException.class,
                () -> TemplateRequest.builder().name("n").html("h").build());
        assertThrows(IllegalArgumentException.class,
                () -> TemplateRequest.builder().name("n").subject("s").build());
    }
}
