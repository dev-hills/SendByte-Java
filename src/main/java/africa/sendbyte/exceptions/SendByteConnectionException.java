package africa.sendbyte.exceptions;

/**
 * Raised when the SDK cannot reach the SendByte API at all (DNS failure,
 * connection reset, timeout, etc.). Carries no HTTP status or error code.
 */
public class SendByteConnectionException extends SendByteException {
    public SendByteConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
