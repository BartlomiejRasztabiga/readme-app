package pl.infinitefuture.reading.books.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import pl.infinitefuture.reading.DatabaseConverters;

@Database(entities = {Book.class}, version = 2)
@TypeConverters({DatabaseConverters.class})
public abstract class BooksDatabase extends RoomDatabase {

    private static BooksDatabase instance;

    public abstract BooksDao booksDao();

    private static final Object sLock = new Object();

    public static BooksDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        BooksDatabase.class, "Books.db")
                        .fallbackToDestructiveMigration() //TODO Remove
                        .build();
            }
            return instance;
        }
    }
}
