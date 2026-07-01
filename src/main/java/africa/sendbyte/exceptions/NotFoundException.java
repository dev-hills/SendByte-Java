package africa.sendbyte.exceptions;

/** {@code not_found} (404): the requested resource does not exist. */
public class NotFoundException extends SendByteException {
    public NotFoundException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
