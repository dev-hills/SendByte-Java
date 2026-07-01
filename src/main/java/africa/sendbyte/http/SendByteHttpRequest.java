package africa.sendbyte.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A transport-agnostic description of an HTTP request to the SendByte API.
 *
 * <p>Resources build one of these and hand it to a {@link RequestExecutor};
 * the executor serializes {@link #body()}, applies auth and base URL via the
 * {@link HttpTransport}, and deserializes the response.
 *
 * <p>Internal SDK type.
 */
public final class SendByteHttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> query;
    private final Map<String, String> headers;
    private final Object body;

    private SendByteHttpRequest(Builder b) {
        this.method = b.method;
        this.path = b.path;
        this.query = Collections.unmodifiableMap(b.query);
        this.headers = Collections.unmodifiableMap(b.headers);
        this.body = b.body;
    }

    public String method() {
        return method;
    }

    public String path() {
        return path;
    }

    public Map<String, String> query() {
        return query;
    }

    public Map<String, String> headers() {
        return headers;
    }

    /** The request body as a plain object to be JSON-serialized, or {@code null}. */
    public Object body() {
        return body;
    }

    public static Builder builder(String method, String path) {
        return new Builder(method, path);
    }

    public static final class Builder {
        private final String method;
        private final String path;
        private final Map<String, String> query = new LinkedHashMap<>();
        private final Map<String, String> headers = new LinkedHashMap<>();
        private Object body;

        private Builder(String method, String path) {
            this.method = method;
            this.path = path;
        }

        /** Add a query parameter. Ignored when {@code value} is {@code null}. */
        public Builder query(String name, Object value) {
            if (value != null) {
                this.query.put(name, String.valueOf(value));
            }
            return this;
        }

        public Builder header(String name, String value) {
            if (value != null) {
                this.headers.put(name, value);
            }
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public SendByteHttpRequest build() {
            return new SendByteHttpRequest(this);
        }
    }
}
