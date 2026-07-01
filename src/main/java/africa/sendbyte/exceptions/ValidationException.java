package africa.sendbyte.exceptions;

/** {@code validation_error} (422): one or more request fields failed validation. */
public class ValidationException extends SendByteException {
    public ValidationException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
