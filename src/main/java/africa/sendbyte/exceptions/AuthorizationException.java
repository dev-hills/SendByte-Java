package africa.sendbyte.exceptions;

/** {@code authorization_error} (403): the API key's scope is insufficient. */
public class AuthorizationException extends SendByteException {
    public AuthorizationException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
