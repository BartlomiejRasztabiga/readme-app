package pl.infinitefuture.reading;

import android.content.Context;
import android.support.annotation.NonNull;

import pl.infinitefuture.reading.books.BooksRepository;
import pl.infinitefuture.reading.books.persistence.BooksDatabase;
import pl.infinitefuture.reading.books.persistence.BooksLocalDataSource;
import pl.infinitefuture.reading.books.persistence.BooksRemoteDataSource;
import pl.infinitefuture.reading.sessions.ReadingSessionsRepository;
import pl.infinitefuture.reading.sessions.persistence.ReadingSessionsLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    private Injection() {

    }

    public static BooksRepository provideBooksRepository(@NonNull Context context) {
        checkNotNull(context);
        BooksDatabase database = BooksDatabase.getInstance(context);
        return BooksRepository.getInstance(BooksRemoteDataSource.getInstance(),
                BooksLocalDataSource.getInstance(database.booksDao()));
    }

    public static ReadingSessionsRepository provideReadingSessionsRepository(@NonNull Context context) {
        checkNotNull(context);
        BooksDatabase database = BooksDatabase.getInstance(context);
        return ReadingSessionsRepository.getInstance(ReadingSessionsLocalDataSource
                .getInstance(database.readingSessionsDao()));
    }
}