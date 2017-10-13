package pl.infinitefuture.reading.sessions.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReadingSessionsDao {

    @Query("SELECT * FROM readingSessions")
    List<ReadingSession> getSessions();

    @Query("SELECT * FROM readingSessions WHERE book_id = :bookId")
    List<ReadingSession> getSessionsByBookId(Long bookId);
}
