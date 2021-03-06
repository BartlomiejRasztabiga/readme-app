package pl.infinitefuture.readme.sessions;

import android.databinding.BindingAdapter;
import android.widget.ListView;

import java.util.List;

import pl.infinitefuture.readme.sessions.persistence.ReadingSession;

public class ReadingSessionsListBindings {

    private ReadingSessionsListBindings() {

    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("app:sessions")
    public static void setItems(ListView listView, List<ReadingSession> items) {
        ReadingSessionsAdapter adapter = (ReadingSessionsAdapter) listView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }
}