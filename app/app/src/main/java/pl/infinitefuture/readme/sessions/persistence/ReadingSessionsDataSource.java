package pl.infinitefuture.readme.sessions.persistence;

import android.support.annotation.NonNull;

import java.util.List;

public interface ReadingSessionsDataSource {

    void getSessions(@NonNull Long bookId, @NonNull LoadSessionsCallback callback);

    void saveSession(@NonNull ReadingSession session);

    void saveSession(@NonNull ReadingSession session,
                     @NonNull SaveSessionCallback callback);

    interface LoadSessionsCallback {

        void onSessionsLoaded(List<ReadingSession> sessions);

        void onDataNotAvailable();
    }

    interface SaveSessionCallback {

        default void onSessionSaved() {
        }

        default void onDataNotAvailable() {
        }
    }
}