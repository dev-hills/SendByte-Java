package africa.sendbyte;

import java.time.Duration;

/**
 * Immutable configuration for a {@link SendByteClient}.
 *
 * <p>Build one with {@link #builder()}. Only the API key is required; the base URL
 * (overridable for testing or self-hosted gateways), timeouts, and User-Agent all
 * have sensible defaults.
 */
public final class SendByteClientOptions {

    /** Default production API base URL, including the {@code /v1} version prefix. */
    public static final String DEFAULT_BASE_URL = "https://api.sendbyte.africa/v1";

    private final String apiKey;
    private final String baseUrl;
    private final String userAgent;
    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final Duration writeTimeout;

    private SendByteClientOptions(Builder b) {
        if (b.apiKey == null || b.apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("A SendByte API key is required.");
        }
        this.apiKey = b.apiKey;
        this.baseUrl = stripTrailingSlash(b.baseUrl);
        this.userAgent = b.userAgent;
        this.connectTimeout = b.connectTimeout;
        this.readTimeout = b.readTimeout;
        this.writeTimeout = b.writeTimeout;
    }

    private static String stripTrailingSlash(String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String apiKey() {
        return apiKey;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String userAgent() {
        return userAgent;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public Duration readTimeout() {
        return readTimeout;
    }

    public Duration writeTimeout() {
        return writeTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiKey;
        private String baseUrl = DEFAULT_BASE_URL;
        private String userAgent = "sendbyte-java/0.1.0";
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration readTimeout = Duration.ofSeconds(30);
        private Duration writeTimeout = Duration.ofSeconds(30);

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /** Override the API base URL. Any trailing slash is stripped. */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder writeTimeout(Duration writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public SendByteClientOptions build() {
            return new SendByteClientOptions(this);
        }
    }
}
