package africa.sendbyte.exceptions;

/** {@code suppressed_recipient} (422): a recipient is on the project's suppression list. */
public class SuppressedRecipientException extends SendByteException {
    public SuppressedRecipientException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
