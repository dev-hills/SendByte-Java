package africa.sendbyte.templates;

/**
 * A saved, server-side email template.
 *
 * <p>{@link Templates#get(String)} returns the full record including the stored
 * {@code html} and {@code mjml} source; create/list responses populate the summary
 * fields (id, name, subject, version, format, timestamps).
 */
public final class Template {

    private String id;
    private String name;
    private String subject;
    private Integer version;
    private String format;
    private String html;
    private String text;
    private String mjml;
    private String createdAt;
    private String updatedAt;

    /** Unique template identifier (a UUID). Pass as {@code template_id} when sending. */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** The unique, human-readable template name within the project. */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** The subject line, which may contain Handlebars variables. */
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** The version counter, bumped on each update. */
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /** The stored body format, e.g. {@code html} or {@code mjml}. */
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /** The stored HTML body. Present on {@link Templates#get}; {@code null} on create/list. */
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

    /** The original MJML source, if the template was authored in MJML. */
    public String getMjml() {
        return mjml;
    }

    public void setMjml(String mjml) {
        this.mjml = mjml;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
