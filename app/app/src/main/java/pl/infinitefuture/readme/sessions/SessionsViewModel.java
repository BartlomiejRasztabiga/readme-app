package pl.infinitefuture.readme.sessions;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.books.BooksRepository;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.books.persistence.BooksDataSource;
import pl.infinitefuture.readme.sessions.persistence.ReadingSession;
import pl.infinitefuture.readme.sessions.persistence.ReadingSessionsDataSource;

public class SessionsViewModel extends AndroidViewModel {

    public final ObservableList<ReadingSession> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableBoolean empty = new ObservableBoolean(false);

    public final ObservableField<Long> bookId = new ObservableField<>();

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final ReadingSessionsRepository mSessionsRepository;

    private final BooksRepository mBooksRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    public SessionsViewModel(
            Application context,
            ReadingSessionsRepository sessionsRepository,
            BooksRepository booksRepository) {
        super(context);
        mSessionsRepository = sessionsRepository;
        mBooksRepository = booksRepository;
    }

    public void start(Long bookId) {
        loadSessions(bookId);
    }

    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    private void loadSessions(Long bookId) {
        this.bookId.set(bookId);
        dataLoading.set(true);

        mSessionsRepository.getSessions(bookId, new ReadingSessionsDataSource.LoadSessionsCallback() {
            @Override
            public void onSessionsLoaded(List<ReadingSession> sessions) {
                dataLoading.set(false);

                mIsDataLoadingError.set(false);

                items.clear();
                items.addAll(Lists.reverse(sessions));
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                dataLoading.set(false);
                mIsDataLoadingError.set(true);
                mSnackbarText.setValue(R.string.loading_sessions_error);
            }
        });
    }

    public void removeSession(ReadingSession session) {
        mSessionsRepository.deleteSession(session.getId());

        // update readPages in book
        mBooksRepository.getBook(this.bookId.get(), new BooksDataSource.GetBookCallback() {
            @Override
            public void onBookLoaded(Book book) {
                book.addReadPages(-session.getReadPages()); //subtract readPages
                if(book.isCompleted()) book.setCompleted(false); //reverse completion
                mBooksRepository.updateBook(book);
            }

            @Override
            public void onDataNotAvailable() {
                // ignore for now
            }
        });

    }
}
