package africa.sendbyte.domains;

import java.util.List;

/** The result of {@link Domains#list()}: all sending domains on the account. */
public final class DomainList {

    private List<Domain> data;

    /** The registered domains. */
    public List<Domain> getData() {
        return data;
    }

    public void setData(List<Domain> data) {
        this.data = data;
    }
}
