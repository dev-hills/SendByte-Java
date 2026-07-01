package africa.sendbyte.templates;

/**
 * The definition used to {@link Templates#create(TemplateRequest) create} or
 * {@link Templates#update(String, TemplateRequest) update} a template.
 *
 * <p>Provide a {@code name}, a {@code subject}, and at least one body — {@code html},
 * {@code text}, or {@code mjml}. Subject and bodies may contain Handlebars variables.
 *
 * <pre>{@code
 * TemplateRequest req = TemplateRequest.builder()
 *     .name("welcome")
 *     .subject("Welcome to PayLink, {{first_name}}!")
 *     .html("<p>Hi {{first_name}}, your account is ready.</p>")
 *     .text("Hi {{first_name}}, your account is ready.")
 *     .build();
 * }</pre>
 */
public final class TemplateRequest {

    private final String name;
    private final String subject;
    private final String html;
    private final String text;
    private final String mjml;

    private TemplateRequest(Builder b) {
        this.name = b.name;
        this.subject = b.subject;
        this.html = b.html;
        this.text = b.text;
        this.mjml = b.mjml;
    }

    public String getName() {
        return name;
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

    public String getMjml() {
        return mjml;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String subject;
        private String html;
        private String text;
        private String mjml;

        /** A unique, human-readable template name, e.g. {@code welcome}. */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /** The subject line; supports Handlebars variables. */
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        /** HTML body with Handlebars syntax. */
        public Builder html(String html) {
            this.html = html;
            return this;
        }

        /** Plain-text alternative body. Recommended alongside {@code html}. */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /** MJML source; compiled to responsive HTML at save time. */
        public Builder mjml(String mjml) {
            this.mjml = mjml;
            return this;
        }

        public TemplateRequest build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("'name' is required.");
            }
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("'subject' is required.");
            }
            if (html == null && text == null && mjml == null) {
                throw new IllegalArgumentException("One of 'html', 'text', or 'mjml' is required.");
            }
            return new TemplateRequest(this);
        }
    }
}
