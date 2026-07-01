package africa.sendbyte.templates;

import java.util.List;

/** The result of {@link Templates#list()}: all templates in the project. */
public final class TemplateList {

    private List<Template> data;
    private boolean hasMore;

    /** The templates in the project. */
    public List<Template> getData() {
        return data;
    }

    public void setData(List<Template> data) {
        this.data = data;
    }

    /** {@code true} if the API paginated the result and more templates exist. */
    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
