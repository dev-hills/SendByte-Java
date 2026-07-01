package africa.sendbyte.exceptions;

/** {@code authentication_error} (401): missing, revoked, or malformed API key. */
public class AuthenticationException extends SendByteException {
    public AuthenticationException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
