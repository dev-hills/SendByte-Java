package africa.sendbyte.exceptions;

/** {@code idempotency_conflict} (409): idempotency key reused with a different payload. */
public class IdempotencyConflictException extends SendByteException {
    public IdempotencyConflictException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
