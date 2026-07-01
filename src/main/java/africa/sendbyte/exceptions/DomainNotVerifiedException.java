package africa.sendbyte.exceptions;

/** {@code domain_not_verified} (403): live send attempted from an unverified domain. */
public class DomainNotVerifiedException extends SendByteException {
    public DomainNotVerifiedException(String message, String code, int status, String docsUrl, String requestId) {
        super(message, code, status, docsUrl, requestId);
    }
}
