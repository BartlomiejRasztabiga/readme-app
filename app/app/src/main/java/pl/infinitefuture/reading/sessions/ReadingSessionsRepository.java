package pl.infinitefuture.reading.sessions;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import pl.infinitefuture.reading.sessions.persistence.ReadingSession;
import pl.infinitefuture.reading.sessions.persistence.ReadingSessionsDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReadingSessionsRepository implements ReadingSessionsDataSource {

    private static volatile ReadingSessionsRepository instance = null;

    private final ReadingSessionsDataSource mSessionsLocalDataSource;

    private ReadingSessionsRepository(@NonNull ReadingSessionsDataSource sessionsLocalDataSource) {
        mSessionsLocalDataSource = checkNotNull(sessionsLocalDataSource);
    }

    public static ReadingSessionsRepository getInstance(ReadingSessionsDataSource sessionsLocalDataSource) {
        if (instance == null) {
            synchronized (ReadingSessionsRepository.class) {
                if (instance == null) {
                    instance = new ReadingSessionsRepository(sessionsLocalDataSource);
                }
            }
        }
        return instance;
    }

    @Override
    public void getSessions(@NonNull Long bookId, @NonNull LoadSessionsCallback callback) {
        checkNotNull(callback);

        mSessionsLocalDataSource.getSessions(bookId, new LoadSessionsCallback() {
            @Override
            public void onSessionsLoaded(List<ReadingSession> sessions) {
                callback.onSessionsLoaded(sessions);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveSession(@NonNull ReadingSession session,
                            @NonNull SaveSessionCallback callback) {
        mSessionsLocalDataSource.saveSession(session, new SaveSessionCallback() {
            @Override
            public void onSessionSaved() {
                callback.onSessionSaved();
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveSession(@NonNull ReadingSession session) {
        saveSession(session, new SaveSessionCallback() {});
    }
}
