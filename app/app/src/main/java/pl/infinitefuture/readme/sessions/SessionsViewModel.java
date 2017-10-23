package pl.infinitefuture.readme.sessions;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.sessions.persistence.ReadingSession;
import pl.infinitefuture.readme.sessions.persistence.ReadingSessionsDataSource;

public class SessionsViewModel extends AndroidViewModel {

    public final ObservableList<ReadingSession> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableBoolean empty = new ObservableBoolean(false);

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final ReadingSessionsRepository mSessionsRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    public SessionsViewModel(
            Application context,
            ReadingSessionsRepository repository) {
        super(context);
        mSessionsRepository = repository;
    }

    public void start(Long bookId) {
        loadSessions(bookId);
    }

    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    private void loadSessions(Long bookId) {
        dataLoading.set(true);

        mSessionsRepository.getSessions(bookId, new ReadingSessionsDataSource.LoadSessionsCallback() {
            @Override
            public void onSessionsLoaded(List<ReadingSession> sessions) {
                dataLoading.set(false);

                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(sessions);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                dataLoading.set(false);
                mIsDataLoadingError.set(true);
                mSnackbarText.setValue(R.string.loading_sessions_error);
            }
        });
    }
}
