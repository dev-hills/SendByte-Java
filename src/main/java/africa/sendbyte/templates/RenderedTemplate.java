package africa.sendbyte.templates;

/**
 * The rendered output of {@link Templates#render(RenderRequest)} or
 * {@link Templates#preview(String, java.util.Map)} — the final subject and bodies a
 * recipient would receive, with variables interpolated.
 */
public final class RenderedTemplate {

    private String subject;
    private String html;
    private String text;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

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
}
