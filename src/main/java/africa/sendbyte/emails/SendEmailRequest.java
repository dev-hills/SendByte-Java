package africa.sendbyte.emails;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The payload for {@link Emails#send(SendEmailRequest)}.
 *
 * <p>Built with a fluent {@link #builder()} because most fields are optional.
 * At minimum, provide {@code from}, at least one {@code to} recipient, and one of
 * {@code html}, {@code text}, or {@code templateId}.
 *
 * <pre>{@code
 * SendEmailRequest req = SendEmailRequest.builder()
 *     .from("PayLink <receipts@paylink.ng>")
 *     .to("amaka@halo.ng")
 *     .subject("Receipt for ₦45,000")
 *     .html("<p>Payment received.</p>")
 *     .text("Payment received.")
 *     .tags("receipt", "payment")
 *     .idempotencyKey("order-4421-receipt")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SendEmailRequest {

    private final String from;
    private final List<String> to;
    private final String subject;
    private final String html;
    private final String text;
    private final String templateId;
    private final Map<String, Object> variables;
    private final List<String> cc;
    private final List<String> bcc;
    private final String replyTo;
    private final Map<String, String> headers;
    private final List<Attachment> attachments;
    private final List<String> tags;
    private final String scheduledAt;
    private final String idempotencyKey;

    private SendEmailRequest(Builder b) {
        this.from = b.from;
        this.to = nullIfEmpty(b.to);
        this.subject = b.subject;
        this.html = b.html;
        this.text = b.text;
        this.templateId = b.templateId;
        this.variables = nullIfEmpty(b.variables);
        this.cc = nullIfEmpty(b.cc);
        this.bcc = nullIfEmpty(b.bcc);
        this.replyTo = b.replyTo;
        this.headers = nullIfEmpty(b.headers);
        this.attachments = nullIfEmpty(b.attachments);
        this.tags = nullIfEmpty(b.tags);
        this.scheduledAt = b.scheduledAt;
        this.idempotencyKey = b.idempotencyKey;
    }

    private static <T> List<T> nullIfEmpty(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }

    private static <K, V> Map<K, V> nullIfEmpty(Map<K, V> map) {
        return (map == null || map.isEmpty()) ? null : map;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getHtml() {
        return html;
    }

    public String getText() {
        return text;
    }

    public String getTemplateId() {
        return templateId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getScheduledAt() {
        return scheduledAt;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String from;
        private final List<String> to = new ArrayList<>();
        private String subject;
        private String html;
        private String text;
        private String templateId;
        private final Map<String, Object> variables = new LinkedHashMap<>();
        private final List<String> cc = new ArrayList<>();
        private final List<String> bcc = new ArrayList<>();
        private String replyTo;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private final List<Attachment> attachments = new ArrayList<>();
        private final List<String> tags = new ArrayList<>();
        private String scheduledAt;
        private String idempotencyKey;

        /** Sender address. Bare ({@code receipts@paylink.ng}) or display-name ({@code PayLink <receipts@paylink.ng>}). */
        public Builder from(String from) {
            this.from = from;
            return this;
        }

        /** Add one or more recipients. Repeatable; up to 50 total. */
        public Builder to(String... to) {
            this.to.addAll(Arrays.asList(to));
            return this;
        }

        public Builder to(List<String> to) {
            this.to.addAll(to);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder html(String html) {
            this.html = html;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /** Send using a saved template (UUID or name) instead of inline {@code html}/{@code text}. */
        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        /** Add a single template variable. Repeatable. */
        public Builder variable(String key, Object value) {
            this.variables.put(key, value);
            return this;
        }

        public Builder variables(Map<String, ?> variables) {
            this.variables.putAll(variables);
            return this;
        }

        public Builder cc(String... cc) {
            this.cc.addAll(Arrays.asList(cc));
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc.addAll(cc);
            return this;
        }

        public Builder bcc(String... bcc) {
            this.bcc.addAll(Arrays.asList(bcc));
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc.addAll(bcc);
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        /** Add a single custom MIME header. Repeatable. */
        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder addAttachment(Attachment attachment) {
            this.attachments.add(attachment);
            return this;
        }

        public Builder attachments(List<Attachment> attachments) {
            this.attachments.addAll(attachments);
            return this;
        }

        public Builder tags(String... tags) {
            this.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags.addAll(tags);
            return this;
        }

        /** Schedule delivery for an ISO 8601 datetime string, e.g. {@code 2026-06-01T09:00:00Z}. */
        public Builder scheduledAt(String scheduledAt) {
            this.scheduledAt = scheduledAt;
            return this;
        }

        /** Schedule delivery for a specific {@link Instant}. */
        public Builder scheduledAt(Instant scheduledAt) {
            this.scheduledAt = scheduledAt == null ? null : scheduledAt.toString();
            return this;
        }

        /** A unique key (UUID recommended) that makes retries safe against duplicate sends. */
        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public SendEmailRequest build() {
            if (from == null || from.trim().isEmpty()) {
                throw new IllegalArgumentException("'from' is required.");
            }
            if (to.isEmpty()) {
                throw new IllegalArgumentException("At least one 'to' recipient is required.");
            }
            if (to.size() > 50) {
                throw new IllegalArgumentException("A maximum of 50 recipients is allowed per send.");
            }
            if (html == null && text == null && templateId == null) {
                throw new IllegalArgumentException("One of 'html', 'text', or 'templateId' is required.");
            }
            if (attachments.size() > 10) {
                throw new IllegalArgumentException("A maximum of 10 attachments is allowed per send.");
            }
            return new SendEmailRequest(this);
        }
    }
}
