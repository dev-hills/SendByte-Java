package africa.sendbyte.http;

/**
 * A minimal, transport-agnostic view of an HTTP response from the SendByte API.
 *
 * <p>Internal SDK type.
 */
public final class SendByteResponse {

    private final int statusCode;
    private final String body;
    private final HeaderLookup headers;

    public SendByteResponse(int statusCode, String body, HeaderLookup headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int statusCode() {
        return statusCode;
    }

    public String body() {
        return body;
    }

    /** Case-insensitive header lookup; returns {@code null} when absent. */
    public String header(String name) {
        return headers == null ? null : headers.get(name);
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /** Functional lookup so transports can adapt their own header representation. */
    public interface HeaderLookup {
        String get(String name);
    }
}
