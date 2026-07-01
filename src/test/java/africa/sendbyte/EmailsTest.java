package africa.sendbyte;

import africa.sendbyte.emails.Email;
import africa.sendbyte.emails.EmailList;
import africa.sendbyte.emails.EmailStatus;
import africa.sendbyte.emails.ListEmailsParams;
import africa.sendbyte.emails.SendEmailRequest;
import africa.sendbyte.exceptions.NotFoundException;
import africa.sendbyte.exceptions.RateLimitException;
import africa.sendbyte.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailsTest {

    @Test
    void sendSerializesSnakeCaseAndParsesResponse() {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"em_123\",\"status\":\"queued\",\"sandbox\":true," +
                        "\"created_at\":\"2026-06-13T09:14:07Z\"}");
        SendByteClient client = new SendByteClient(transport);

        Email email = client.emails().send(SendEmailRequest.builder()
                .from("PayLink <receipts@paylink.ng>")
                .to("amaka@halo.ng")
                .subject("Receipt")
                .html("<p>Paid.</p>")
                .replyTo("support@paylink.ng")
                .idempotencyKey("order-4421")
                .tags("receipt", "payment")
                .build());

        // Response parsed correctly.
        assertEquals("em_123", email.getId());
        assertEquals(EmailStatus.QUEUED, email.status());
        assertTrue(email.isSandbox());

        // Request went to the right place with the right verb.
        assertEquals("POST", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/emails"), transport.lastUrl);

        // Body uses the API's snake_case wire format and omits nulls.
        String body = transport.lastBody;
        assertTrue(body.contains("\"reply_to\""), body);
        assertTrue(body.contains("\"idempotency_key\""), body);
        assertTrue(body.contains("\"from\""), body);
        assertFalse(body.contains("\"template_id\""), body);
    }

    @Test
    void getParsesEventsTimeline() {
        StubTransport transport = new StubTransport(200,
                "{\"id\":\"em_9\",\"status\":\"delivered\",\"sandbox\":false," +
                        "\"to\":[\"a@b.ng\"],\"html\":\"<p>hi</p>\"," +
                        "\"events\":[{\"type\":\"email.sent\",\"created_at\":\"2026-06-13T09:14:08Z\"}," +
                        "{\"type\":\"email.delivered\",\"payload\":{\"delivered_in_ms\":1180}," +
                        "\"created_at\":\"2026-06-13T09:14:09Z\"}]}");
        SendByteClient client = new SendByteClient(transport);

        Email email = client.emails().get("em_9");

        assertEquals("GET", transport.lastMethod);
        assertTrue(transport.lastUrl.endsWith("/emails/em_9"), transport.lastUrl);
        assertEquals(EmailStatus.DELIVERED, email.status());
        assertNotNull(email.getEvents());
        assertEquals(2, email.getEvents().size());
        assertEquals("email.delivered", email.getEvents().get(1).getType());
        assertEquals(1180, email.getEvents().get(1).getPayload().get("delivered_in_ms"));
    }

    @Test
    void listBuildsQueryStringAndReadsHasMore() {
        StubTransport transport = new StubTransport(200,
                "{\"data\":[{\"id\":\"em_1\",\"status\":\"delivered\"}],\"has_more\":true}");
        SendByteClient client = new SendByteClient(transport);

        EmailList result = client.emails().list(ListEmailsParams.builder()
                .limit(50)
                .status(EmailStatus.DELIVERED)
                .after("em_prev")
                .build());

        assertTrue(transport.lastUrl.contains("limit=50"), transport.lastUrl);
        assertTrue(transport.lastUrl.contains("status=delivered"), transport.lastUrl);
        assertTrue(transport.lastUrl.contains("after=em_prev"), transport.lastUrl);
        assertTrue(result.isHasMore());
        assertEquals(1, result.getData().size());
        assertEquals("em_1", result.getData().get(0).getId());
    }

    @Test
    void listWithoutParamsHasNoQueryString() {
        StubTransport transport = new StubTransport(200, "{\"data\":[],\"has_more\":false}");
        SendByteClient client = new SendByteClient(transport);

        client.emails().list();

        assertFalse(transport.lastUrl.contains("?"), transport.lastUrl);
    }

    @Test
    void validationErrorMapsToTypedException() {
        StubTransport transport = new StubTransport(422,
                "{\"error\":{\"code\":\"validation_error\",\"message\":\"'subject' is required\"," +
                        "\"docs_url\":\"https://docs.sendbyte.africa/errors/validation_error\"}}",
                Collections.singletonMap("x-request-id", "req_abc"));
        SendByteClient client = new SendByteClient(transport);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                client.emails().send(SendEmailRequest.builder()
                        .from("a@b.ng").to("c@d.ng").html("<p>x</p>").build()));

        assertEquals("validation_error", ex.getCode());
        assertEquals(422, ex.getStatus());
        assertEquals("req_abc", ex.getRequestId());
        assertEquals("https://docs.sendbyte.africa/errors/validation_error", ex.getDocsUrl());
        assertTrue(ex.getMessage().contains("subject"));
    }

    @Test
    void notFoundMapsToTypedException() {
        StubTransport transport = new StubTransport(404,
                "{\"error\":{\"code\":\"not_found\",\"message\":\"No such email\"}}");
        SendByteClient client = new SendByteClient(transport);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> client.emails().get("em_missing"));
        assertEquals(404, ex.getStatus());
    }

    @Test
    void rateLimitExposesRetryAfter() {
        StubTransport transport = new StubTransport(429,
                "{\"error\":{\"code\":\"rate_limit_exceeded\",\"message\":\"slow down\"}}",
                Collections.singletonMap("Retry-After", "17"));
        SendByteClient client = new SendByteClient(transport);

        RateLimitException ex = assertThrows(RateLimitException.class,
                () -> client.emails().get("em_1"));
        assertEquals(Integer.valueOf(17), ex.getRetryAfterSeconds());
    }

    @Test
    void asyncSendReturnsResult() throws Exception {
        StubTransport transport = new StubTransport(201,
                "{\"id\":\"em_async\",\"status\":\"queued\"}");
        SendByteClient client = new SendByteClient(transport);

        Email email = client.emails().sendAsync(SendEmailRequest.builder()
                .from("a@b.ng").to("c@d.ng").text("hi").build()).get();

        assertEquals("em_async", email.getId());
    }
}
