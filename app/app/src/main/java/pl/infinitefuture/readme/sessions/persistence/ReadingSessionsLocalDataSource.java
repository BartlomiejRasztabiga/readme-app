package pl.infinitefuture.readme.sessions.persistence;

import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReadingSessionsLocalDataSource implements ReadingSessionsDataSource {

    private static ReadingSessionsLocalDataSource instance;

    private ReadingSessionsDao mSessionsDao;

    private ReadingSessionsLocalDataSource(@NonNull ReadingSessionsDao sessionsDao) {
        mSessionsDao = sessionsDao;
    }

    public static ReadingSessionsLocalDataSource getInstance(@NonNull ReadingSessionsDao sessionsDao) {
        if (instance == null) {
            instance = new ReadingSessionsLocalDataSource(sessionsDao);
        }
        return instance;
    }

    @Override
    public void getSessions(@NonNull Long sessionId, @NonNull LoadSessionsCallback callback) {
        Observable.fromCallable(() -> mSessionsDao.getSessionsByBookId(sessionId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sessions -> {
                    if (sessions == null) {
                        callback.onDataNotAvailable();
                    } else {
                        callback.onSessionsLoaded(sessions);
                    }
                });
    }

    @Override
    public void saveSession(@NonNull ReadingSession session,
                            @NonNull SaveSessionCallback callback) {
        checkNotNull(session);
        Observable.fromCallable(() -> mSessionsDao.insertSession(session))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> callback.onSessionSaved());
    }

    @Override
    public void saveSession(@NonNull ReadingSession session) {
        saveSession(session, new SaveSessionCallback() {
        });
    }

    @Override
    public void deleteSession(@NonNull Long sessionId) {
        Observable.fromCallable(() -> {
            mSessionsDao.deleteSessionById(sessionId);
            return Observable.empty();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
