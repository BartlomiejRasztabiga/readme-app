package pl.infinitefuture.readme.sessions.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReadingSessionsDao {

    @Query("SELECT * FROM readingSessions")
    List<ReadingSession> getSessions();

    @Query("SELECT * FROM readingSessions WHERE book_id = :bookId ORDER BY date ASC")
    List<ReadingSession> getSessionsByBookId(Long bookId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertSession(ReadingSession session);
}
