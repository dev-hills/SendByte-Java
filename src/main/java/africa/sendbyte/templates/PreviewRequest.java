package africa.sendbyte.templates;

import java.util.Map;

/** Request body for {@link Templates#preview(String, Map)}: {@code { "variables": {...} }}. */
final class PreviewRequest {

    private final Map<String, Object> variables;

    PreviewRequest(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }
}
