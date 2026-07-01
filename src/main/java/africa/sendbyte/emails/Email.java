package africa.sendbyte.emails;

import java.util.List;

/**
 * A sent email as returned by {@link Emails#send}, {@link Emails#get}, and the
 * items of {@link Emails#list}.
 *
 * <p>Send and list responses populate a summary subset of these fields; a
 * {@link Emails#get(String)} response additionally includes the rendered
 * {@code html}/{@code text} bodies and the full {@code events} timeline.
 */
public final class Email {

    private String id;
    private String from;
    private List<String> to;
    private String subject;
    private String status;
    private boolean sandbox;
    private String html;
    private String text;
    private String scheduledAt;
    private String createdAt;
    private List<EmailEvent> events;

    /** Unique email identifier (prefixed {@code em_}). */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** Raw status string, e.g. {@code queued}. See {@link #status()} for the typed value. */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /** The status as a typed enum, mapping unknown values to {@link EmailStatus#UNKNOWN}. */
    public EmailStatus status() {
        return EmailStatus.fromWire(status);
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    /** Rendered HTML body. Present on {@link Emails#get}; {@code null} on send/list responses. */
    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /** ISO 8601 scheduled send time, or {@code null} for immediate delivery. */
    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /** Chronological lifecycle timeline. Present on {@link Emails#get}; {@code null} otherwise. */
    public List<EmailEvent> getEvents() {
        return events;
    }

    public void setEvents(List<EmailEvent> events) {
        this.events = events;
    }
}
