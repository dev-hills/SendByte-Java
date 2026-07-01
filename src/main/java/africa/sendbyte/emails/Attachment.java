package africa.sendbyte.emails;

import java.util.Base64;

/**
 * A file attachment on an outgoing email.
 *
 * <p>{@code content} must be Base64-encoded. Use {@link #of(String, String, byte[])}
 * to build one directly from raw bytes.
 */
public final class Attachment {

    private final String filename;
    private final String content;
    private final String contentType;

    public Attachment(String filename, String content, String contentType) {
        this.filename = filename;
        this.content = content;
        this.contentType = contentType;
    }

    /** Build an attachment from raw bytes, Base64-encoding them for you. */
    public static Attachment of(String filename, String contentType, byte[] bytes) {
        return new Attachment(filename, Base64.getEncoder().encodeToString(bytes), contentType);
    }

    public String getFilename() {
        return filename;
    }

    /** Base64-encoded file content. */
    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }
}
