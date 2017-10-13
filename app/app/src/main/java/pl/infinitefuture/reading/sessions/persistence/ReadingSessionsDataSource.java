package pl.infinitefuture.reading.sessions.persistence;

import android.support.annotation.NonNull;

import java.util.List;

public interface ReadingSessionsDataSource {

    interface LoadSessionsCallback {

        void onSessionsLoaded(List<ReadingSession> sessions);

        void onDataNotAvailable();
    }

    void getSessions(@NonNull LoadSessionsCallback callback, @NonNull Long bookId);

    void saveSession(@NonNull ReadingSession session, @NonNull Long bookId);
}
