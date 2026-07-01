package africa.sendbyte.emails;

import java.util.List;

/**
 * A single page of results from {@link Emails#list}.
 *
 * <p>For cursor pagination, take the {@code id} of the last item in {@link #getData()}
 * and pass it as {@code after} on the next call while {@link #isHasMore()} is true.
 */
public final class EmailList {

    private List<Email> data;
    private boolean hasMore;

    /** The emails on this page, newest-first. */
    public List<Email> getData() {
        return data;
    }

    public void setData(List<Email> data) {
        this.data = data;
    }

    /** {@code true} if more emails exist older than the last item on this page. */
    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
