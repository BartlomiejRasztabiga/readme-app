package pl.infinitefuture.reading.sessions.persistence;

import android.support.annotation.NonNull;

import java.util.List;

public interface ReadingSessionsDataSource {

    interface LoadSessionsCallback {

        void onSessionsLoaded(List<ReadingSession> sessions);

        void onDataNotAvailable();
    }

    interface SaveSessionCallback {

        default void onSessionSaved() {}

        default void onDataNotAvailable() {}
    }

    void getSessions(@NonNull Long bookId, @NonNull LoadSessionsCallback callback);

    void saveSession(@NonNull Long bookId, @NonNull ReadingSession session);

    void saveSession(@NonNull Long bookId, @NonNull ReadingSession session,
                     @NonNull SaveSessionCallback callback);
}
