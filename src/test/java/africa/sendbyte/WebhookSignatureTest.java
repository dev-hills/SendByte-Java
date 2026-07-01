package africa.sendbyte;

import africa.sendbyte.webhooks.WebhookSignature;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebhookSignatureTest {

    private static final String SECRET = "whsec_test_secret";
    private static final String BODY = "{\"type\":\"email.delivered\",\"data\":{\"email_id\":\"em_1\"}}";

    @Test
    void acceptsValidSignature() {
        long now = Instant.now().getEpochSecond();
        String header = WebhookSignature.sign(SECRET, now, BODY.getBytes(StandardCharsets.UTF_8));

        assertTrue(WebhookSignature.verify(SECRET, header, BODY));
    }

    @Test
    void rejectsTamperedBody() {
        long now = Instant.now().getEpochSecond();
        String header = WebhookSignature.sign(SECRET, now, BODY.getBytes(StandardCharsets.UTF_8));

        assertFalse(WebhookSignature.verify(SECRET, header, BODY + " "));
    }

    @Test
    void rejectsWrongSecret() {
        long now = Instant.now().getEpochSecond();
        String header = WebhookSignature.sign(SECRET, now, BODY.getBytes(StandardCharsets.UTF_8));

        assertFalse(WebhookSignature.verify("whsec_other", header, BODY));
    }

    @Test
    void rejectsExpiredTimestamp() {
        long tenMinutesAgo = Instant.now().getEpochSecond() - 600;
        String header = WebhookSignature.sign(SECRET, tenMinutesAgo, BODY.getBytes(StandardCharsets.UTF_8));

        // Default tolerance is 5 minutes, so this is rejected...
        assertFalse(WebhookSignature.verify(SECRET, header, BODY));
        // ...but with the age check disabled the signature itself still validates.
        assertTrue(WebhookSignature.verify(SECRET, header, BODY.getBytes(StandardCharsets.UTF_8), Duration.ZERO));
    }

    @Test
    void rejectsMalformedHeader() {
        assertFalse(WebhookSignature.verify(SECRET, "not-a-valid-header", BODY));
        assertFalse(WebhookSignature.verify(SECRET, null, BODY));
    }
}
