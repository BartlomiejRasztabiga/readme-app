package pl.infinitefuture.reading.books.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import pl.infinitefuture.reading.DatabaseConverters;
import pl.infinitefuture.reading.sessions.persistence.ReadingSession;
import pl.infinitefuture.reading.sessions.persistence.ReadingSessionsDao;

@Database(entities = {Book.class, ReadingSession.class}, version = 7)
@TypeConverters({DatabaseConverters.class})
public abstract class BooksDatabase extends RoomDatabase {

    private static BooksDatabase instance;

    public abstract BooksDao booksDao();

    public abstract ReadingSessionsDao readingSessionsDao();

    private static final Object sLock = new Object();

    public static BooksDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        BooksDatabase.class, "Books.db")
                        .addMigrations(MIGRATION_7_8)
                        .build();
            }
            return instance;
        }
    }

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // ignore for now
        }
    };
}
