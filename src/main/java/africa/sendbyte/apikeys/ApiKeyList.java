package africa.sendbyte.apikeys;

import java.util.List;

/** The result of {@link ApiKeys#list()}: all active (non-revoked) keys on the account. */
public final class ApiKeyList {

    private List<ApiKey> data;

    /** The active keys. Full key values are never included here — only masked prefixes. */
    public List<ApiKey> getData() {
        return data;
    }

    public void setData(List<ApiKey> data) {
        this.data = data;
    }
}
