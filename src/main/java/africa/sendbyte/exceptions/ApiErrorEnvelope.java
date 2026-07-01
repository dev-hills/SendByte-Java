package africa.sendbyte.exceptions;

/**
 * Wire model for the SendByte error envelope:
 * <pre>{ "error": { "code": "...", "message": "...", "docs_url": "..." } }</pre>
 *
 * <p>Internal SDK type used to deserialize error responses.
 */
public final class ApiErrorEnvelope {

    private ApiError error;

    public ApiError getError() {
        return error;
    }

    public void setError(ApiError error) {
        this.error = error;
    }

    public static final class ApiError {
        private String code;
        private String message;
        private String docsUrl;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDocsUrl() {
            return docsUrl;
        }

        public void setDocsUrl(String docsUrl) {
            this.docsUrl = docsUrl;
        }
    }
}
