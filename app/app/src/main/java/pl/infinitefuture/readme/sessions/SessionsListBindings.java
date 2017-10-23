package pl.infinitefuture.readme.sessions;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import pl.infinitefuture.readme.books.BooksAdapter;
import pl.infinitefuture.readme.sessions.persistence.ReadingSession;

public class SessionsListBindings {

    private SessionsListBindings() {

    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:sessions")
    public static void setItems(RecyclerView recyclerView, List<ReadingSession> items) {
        RecyclerViewSessionsAdapter adapter = (RecyclerViewSessionsAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }
}
