package pl.infinitefuture.readme;

import android.content.Context;
import android.support.annotation.NonNull;

import pl.infinitefuture.readme.books.BooksRepository;
import pl.infinitefuture.readme.books.persistence.BooksDatabase;
import pl.infinitefuture.readme.books.persistence.BooksLocalDataSource;
import pl.infinitefuture.readme.sessions.ReadingSessionsRepository;
import pl.infinitefuture.readme.sessions.persistence.ReadingSessionsLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    private Injection() {

    }

    public static BooksRepository provideBooksRepository(@NonNull Context context) {
        checkNotNull(context);
        BooksDatabase database = BooksDatabase.getInstance(context);
        return BooksRepository.getInstance(BooksLocalDataSource.getInstance(database.booksDao()));
    }

    public static ReadingSessionsRepository provideReadingSessionsRepository(@NonNull Context context) {
        checkNotNull(context);
        BooksDatabase database = BooksDatabase.getInstance(context);
        return ReadingSessionsRepository.getInstance(ReadingSessionsLocalDataSource
                .getInstance(database.readingSessionsDao()));
    }
}