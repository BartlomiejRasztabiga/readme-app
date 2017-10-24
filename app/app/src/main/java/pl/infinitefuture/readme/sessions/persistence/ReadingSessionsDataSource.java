package pl.infinitefuture.readme.sessions.persistence;

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

    void getSessions(@NonNull Long sessionId, @NonNull LoadSessionsCallback callback);

    void saveSession(@NonNull ReadingSession session);

    void saveSession(@NonNull ReadingSession session,
                     @NonNull SaveSessionCallback callback);

    void deleteSession(@NonNull Long sessionId);
}
