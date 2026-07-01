package africa.sendbyte.exceptions;

/**
 * {@code rate_limit_exceeded} (429): the per-key request rate was exceeded.
 *
 * <p>{@link #getRetryAfterSeconds()} exposes the value of the {@code Retry-After}
 * response header when present, so you can back off before retrying.
 */
public class RateLimitException extends SendByteException {

    private final Integer retryAfterSeconds;

    public RateLimitException(String message, String code, int status, String docsUrl, String requestId,
                              Integer retryAfterSeconds) {
        super(message, code, status, docsUrl, requestId);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /** Seconds to wait before retrying, from the {@code Retry-After} header, or {@code null}. */
    public Integer getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
