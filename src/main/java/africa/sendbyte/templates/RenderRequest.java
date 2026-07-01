package africa.sendbyte.templates;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The payload for {@link Templates#render(RenderRequest)}: a template body plus
 * variables, compiled and rendered inline without saving anything. Useful for live
 * previewing in an editor.
 *
 * <pre>{@code
 * RenderRequest req = RenderRequest.builder()
 *     .subject("Welcome, {{first_name}}!")
 *     .html("<p>Hi {{first_name}}.</p>")
 *     .variable("first_name", "Amaka")
 *     .build();
 * }</pre>
 */
public final class RenderRequest {

    private final String subject;
    private final String html;
    private final String text;
    private final String mjml;
    private final Map<String, Object> variables;

    private RenderRequest(Builder b) {
        this.subject = b.subject;
        this.html = b.html;
        this.text = b.text;
        this.mjml = b.mjml;
        this.variables = b.variables.isEmpty() ? null : b.variables;
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

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String subject;
        private String html;
        private String text;
        private String mjml;
        private final Map<String, Object> variables = new LinkedHashMap<>();

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

        public Builder mjml(String mjml) {
            this.mjml = mjml;
            return this;
        }

        /** Add a single variable. Repeatable. */
        public Builder variable(String key, Object value) {
            this.variables.put(key, value);
            return this;
        }

        public Builder variables(Map<String, ?> variables) {
            this.variables.putAll(variables);
            return this;
        }

        public RenderRequest build() {
            if (subject == null && html == null && text == null && mjml == null) {
                throw new IllegalArgumentException(
                        "Provide at least one of 'subject', 'html', 'text', or 'mjml' to render.");
            }
            return new RenderRequest(this);
        }
    }
}
