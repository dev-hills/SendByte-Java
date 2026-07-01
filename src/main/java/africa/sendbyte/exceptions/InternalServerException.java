package africa.sendbyte.exceptions;

/** {@code internal_error} (500): a server-side fault occurred. */
public class InternalServerException extends SendByteException {
    public InternalServerException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
