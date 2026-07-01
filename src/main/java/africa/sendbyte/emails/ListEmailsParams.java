package africa.sendbyte.emails;

/**
 * Optional query parameters for {@link Emails#list(ListEmailsParams)}.
 *
 * <pre>{@code
 * ListEmailsParams params = ListEmailsParams.builder()
 *     .limit(100)
 *     .status(EmailStatus.DELIVERED)
 *     .after("em_lastSeenId")
 *     .build();
 * }</pre>
 */
public final class ListEmailsParams {

    private final Integer limit;
    private final String after;
    private final EmailStatus status;

    private ListEmailsParams(Builder b) {
        if (b.limit != null && (b.limit < 1 || b.limit > 100)) {
            throw new IllegalArgumentException("'limit' must be between 1 and 100.");
        }
        this.limit = b.limit;
        this.after = b.after;
        this.status = b.status;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getAfter() {
        return after;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer limit;
        private String after;
        private EmailStatus status;

        /** Page size, 1-100. Defaults to 20 server-side. */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /** Cursor: the {@code id} of the last email from the previous page. */
        public Builder after(String after) {
            this.after = after;
            return this;
        }

        /** Filter to a single delivery status. */
        public Builder status(EmailStatus status) {
            this.status = status;
            return this;
        }

        public ListEmailsParams build() {
            return new ListEmailsParams(this);
        }
    }
}
