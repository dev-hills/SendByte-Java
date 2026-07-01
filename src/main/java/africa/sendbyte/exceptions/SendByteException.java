package africa.sendbyte.exceptions;

/**
 * Base type for every error surfaced by the SendByte SDK.
 *
 * <p>API errors carry a stable, machine-readable {@link #getCode() code} you can
 * branch on, the HTTP {@link #getStatus() status}, a {@link #getDocsUrl() docs URL},
 * and the {@link #getRequestId() request id} (from the {@code x-request-id}
 * response header) to quote when contacting support.
 *
 * <p>Prefer branching on {@link #getCode()} or catching a specific subclass over
 * matching on the human-readable message.
 */
public class SendByteException extends RuntimeException {

    private final String code;
    private final int status;
    private final String docsUrl;
    private final String requestId;

    public SendByteException(String message, String code, int status, String docsUrl, String requestId) {
        super(message);
        this.code = code;
        this.status = status;
        this.docsUrl = docsUrl;
        this.requestId = requestId;
    }

    public SendByteException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
        this.status = 0;
        this.docsUrl = null;
        this.requestId = null;
    }

    /** Stable machine-readable error code, e.g. {@code validation_error}. May be {@code null} for transport errors. */
    public String getCode() {
        return code;
    }

    /** HTTP status code, or {@code 0} for transport-level errors. */
    public int getStatus() {
        return status;
    }

    /** Link to the documentation page for this error, or {@code null}. */
    public String getDocsUrl() {
        return docsUrl;
    }

    /** Value of the {@code x-request-id} response header, or {@code null}. */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Build the most specific exception subclass for an API error response.
     */
    public static SendByteException fromApiError(int status,
                                                 String code,
                                                 String message,
                                                 String docsUrl,
                                                 String requestId,
                                                 Integer retryAfterSeconds) {
        if (code == null) {
            code = "";
        }
        switch (code) {
            case "validation_error":
                return new ValidationException(message, code, status, docsUrl, requestId);
            case "authentication_error":
                return new AuthenticationException(message, code, status, docsUrl, requestId);
            case "authorization_error":
                return new AuthorizationException(message, code, status, docsUrl, requestId);
            case "domain_not_verified":
                return new DomainNotVerifiedException(message, code, status, docsUrl, requestId);
            case "suppressed_recipient":
                return new SuppressedRecipientException(message, code, status, docsUrl, requestId);
            case "not_found":
                return new NotFoundException(message, code, status, docsUrl, requestId);
            case "idempotency_conflict":
                return new IdempotencyConflictException(message, code, status, docsUrl, requestId);
            case "rate_limit_exceeded":
                return new RateLimitException(message, code, status, docsUrl, requestId, retryAfterSeconds);
            case "internal_error":
                return new InternalServerException(message, code, status, docsUrl, requestId);
            default:
                return new SendByteException(message, code, status, docsUrl, requestId);
        }
    }
}
