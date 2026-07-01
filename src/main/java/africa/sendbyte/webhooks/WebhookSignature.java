package africa.sendbyte.webhooks;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;

/**
 * Verifies the signature on inbound SendByte webhook requests.
 *
 * <p>Every delivery carries a {@value #SIGNATURE_HEADER} header shaped like
 * {@code t=<unix_seconds>,v1=<hex_hmac_sha256>}. The signature is an HMAC-SHA256,
 * keyed with your endpoint's {@code whsec_...} secret, over the string
 * {@code "<t>.<raw body>"}. The timestamp guards against replay attacks.
 *
 * <p>Always verify against the <b>raw</b> request body bytes, before any JSON
 * parsing — re-serializing decoded JSON can change the bytes and break the check.
 *
 * <pre>{@code
 * boolean ok = WebhookSignature.verify(
 *     secret,
 *     request.getHeader(WebhookSignature.SIGNATURE_HEADER),
 *     rawBodyBytes);
 * if (!ok) { response.setStatus(401); return; }
 * }</pre>
 */
public final class WebhookSignature {

    /** The header carrying the signature on every webhook delivery. */
    public static final String SIGNATURE_HEADER = "sendbyte-signature";

    /** Default replay tolerance: reject deliveries whose timestamp is older than this. */
    public static final Duration DEFAULT_TOLERANCE = Duration.ofMinutes(5);

    private static final String HMAC_SHA256 = "HmacSHA256";

    private WebhookSignature() {
    }

    /** Verify a string body using the {@link #DEFAULT_TOLERANCE default tolerance}. */
    public static boolean verify(String secret, String signatureHeader, String body) {
        return verify(secret, signatureHeader,
                body == null ? new byte[0] : body.getBytes(StandardCharsets.UTF_8), DEFAULT_TOLERANCE);
    }

    /** Verify a raw byte body using the {@link #DEFAULT_TOLERANCE default tolerance}. */
    public static boolean verify(String secret, String signatureHeader, byte[] body) {
        return verify(secret, signatureHeader, body, DEFAULT_TOLERANCE);
    }

    /**
     * Verify a raw byte body against the signature header.
     *
     * @param secret          the endpoint's {@code whsec_...} signing secret
     * @param signatureHeader the {@value #SIGNATURE_HEADER} header value
     * @param body            the raw request body bytes, before any parsing
     * @param tolerance       maximum age of the signature timestamp; pass
     *                        {@link Duration#ZERO} or a negative value to skip the age check
     * @return {@code true} only if the signature matches and the timestamp is within tolerance
     */
    public static boolean verify(String secret, String signatureHeader, byte[] body, Duration tolerance) {
        if (secret == null || signatureHeader == null || body == null) {
            return false;
        }

        String timestamp = null;
        String provided = null;
        for (String part : signatureHeader.split(",")) {
            int eq = part.indexOf('=');
            if (eq < 0) {
                continue;
            }
            String key = part.substring(0, eq).trim();
            String value = part.substring(eq + 1).trim();
            if ("t".equals(key)) {
                timestamp = value;
            } else if ("v1".equals(key)) {
                provided = value;
            }
        }

        if (timestamp == null || provided == null) {
            return false;
        }

        if (tolerance != null && tolerance.compareTo(Duration.ZERO) > 0) {
            final long ts;
            try {
                ts = Long.parseLong(timestamp);
            } catch (NumberFormatException e) {
                return false;
            }
            long ageSeconds = Instant.now().getEpochSecond() - ts;
            if (Math.abs(ageSeconds) > tolerance.getSeconds()) {
                return false;
            }
        }

        String signedPayload = timestamp + "." + new String(body, StandardCharsets.UTF_8);
        String expected = hmacSha256Hex(secret, signedPayload);
        return constantTimeEquals(expected, provided);
    }

    /**
     * Compute the {@value #SIGNATURE_HEADER} header value for a body. Useful for testing
     * and for services that need to emit SendByte-compatible signatures.
     */
    public static String sign(String secret, long timestampSeconds, byte[] body) {
        String signedPayload = timestampSeconds + "." + new String(body, StandardCharsets.UTF_8);
        return "t=" + timestampSeconds + ",v1=" + hmacSha256Hex(secret, signedPayload);
    }

    private static String hmacSha256Hex(String secret, String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(raw.length * 2);
            for (byte b : raw) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to compute HMAC-SHA256 signature", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        byte[] ab = a.getBytes(StandardCharsets.UTF_8);
        byte[] bb = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(ab, bb);
    }
}
