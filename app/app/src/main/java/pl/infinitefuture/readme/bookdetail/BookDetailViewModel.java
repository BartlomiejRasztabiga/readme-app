package pl.infinitefuture.readme.bookdetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SingleLiveEvent;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.books.BooksRepository;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.books.persistence.BooksDataSource;
import pl.infinitefuture.readme.sessions.ReadingSessionsRepository;
import pl.infinitefuture.readme.sessions.persistence.ReadingSession;
import pl.infinitefuture.readme.sessions.persistence.ReadingSessionsDataSource;

public class BookDetailViewModel extends AndroidViewModel implements BooksDataSource.GetBookCallback {

    public final ObservableField<Book> book = new ObservableField<>();

    public final ObservableField<Long> readPages = new ObservableField<>();

    public final ObservableField<Long> currentPage = new ObservableField<>();

    public final ObservableField<Long> pagesLeftToRead = new ObservableField<>();

    public final ObservableField<Long> daysLeft = new ObservableField<>();

    public final ObservableField<Long> readingTempo = new ObservableField<>();

    public final ObservableBoolean hasGoodReadingTempo = new ObservableBoolean();

    public final ObservableField<Long> readingTempoToMakeIt = new ObservableField<>();

    public final ObservableList<ReadingSession> lastSessions = new ObservableArrayList<>();

    public final ObservableBoolean emptySessions = new ObservableBoolean();

    private final SingleLiveEvent<Void> mEditBookCommand = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mDeleteBookCommand = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mAddSessionCommand = new SingleLiveEvent<>();

    private final BooksRepository mBooksRepository;

    private final ReadingSessionsRepository mSessionsRepository;

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private boolean mIsDataLoading;

    public BookDetailViewModel(Application context, BooksRepository booksRepository,
                               ReadingSessionsRepository sessionsRepository) {
        super(context);
        mBooksRepository = booksRepository;
        mSessionsRepository = sessionsRepository;
    }

    public void deleteBook() {
        if (book.get() != null) {
            mBooksRepository.deleteBook(book.get().getId());
            mDeleteBookCommand.call();
        }
    }

    public void editBook() {
        mEditBookCommand.call();
    }

    public void addSession() {
        mAddSessionCommand.call();
    }
    
    public void showFullSessionsHistory(View view) {
        // TODO
        Toast.makeText(getApplication(), "TODO: ShowFullSessionsHistory", Toast.LENGTH_SHORT).show();
    }

    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    public SingleLiveEvent<Void> getEditBookCommand() {
        return mEditBookCommand;
    }

    public SingleLiveEvent<Void> getDeleteBookCommand() {
        return mDeleteBookCommand;
    }

    public SingleLiveEvent<Void> getAddSessionCommand() {
        return mAddSessionCommand;
    }

    public void setCompleted(boolean completed) {
        if (mIsDataLoading) {
            return;
        }
        Book bookToComplete = this.book.get();
        bookToComplete.setCompleted(completed);

        mBooksRepository.completeBook(bookToComplete);
        showSnackbarMessage(R.string.book_updated);
    }

    public void start(Long bookId) {
        if (bookId != null) {
            mIsDataLoading = true;
            mBooksRepository.getBook(bookId, this);
            mSessionsRepository.getSessions(bookId, new ReadingSessionsDataSource.LoadSessionsCallback() {
                @Override
                public void onSessionsLoaded(List<ReadingSession> sessions) {
                    lastSessions.clear();
                    lastSessions.addAll(sessions);
                    emptySessions.set(lastSessions.isEmpty());
                }

                @Override
                public void onDataNotAvailable() {
                    emptySessions.set(true);
                }
            });
        }
    }

    public void addReadingSession(Long currentPage, Date sessionDate) {
        Long firstPage = book.get().getFirstPage() != null ? book.get().getFirstPage() : 0L;
        Long lastPage = book.get().getLastPage() != null ? book.get().getLastPage() : 0L;
        Long readPages = book.get().getReadPages() != null ? book.get().getReadPages() : 0L;
        Long bookOldCurrentPage = firstPage + readPages;
        Long newReadPages = currentPage - bookOldCurrentPage;

        if (currentPage <= bookOldCurrentPage) {
            //negative delta, something is wrong
            Toast.makeText(getApplication().getApplicationContext(),
                    getApplication().getString(R.string.wrong_pages), Toast.LENGTH_SHORT).show();
            return;
        } else if (newReadPages >= (lastPage - bookOldCurrentPage)) {
            //book is completed
            newReadPages = lastPage - bookOldCurrentPage;
            book.get().setCompleted(true);

            Toast.makeText(getApplication().getApplicationContext(), "TODO: Book completed",
                    Toast.LENGTH_SHORT).show();
        }

        // update book
        book.get().addReadPages(newReadPages);
        mBooksRepository.updateBook(book.get());

        ReadingSession readingSession = new ReadingSession(book.get().getId(), sessionDate, newReadPages);
        mSessionsRepository.saveSession(readingSession, new ReadingSessionsDataSource.SaveSessionCallback() {
            @Override
            public void onSessionSaved() {
                start(book.get().getId());
            }
        });
    }

    public boolean isDataAvailable() {
        return book.get() != null;
    }

    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    @Override
    public void onBookLoaded(Book book) {
        this.book.set(book);
        mIsDataLoading = false;

        // calculate some data

        // calculate pagesLeftToRead
        Long firstPage = book.getFirstPage() != null ? book.getFirstPage() : 0L;
        Long lastPage = book.getLastPage() != null ? book.getLastPage() : 0L;
        Long totalPages = lastPage - firstPage;
        Long readPages = book.getReadPages() != null ? book.getReadPages() : 0L;
        this.pagesLeftToRead.set(totalPages - readPages);

        // calculate readPages
        this.readPages.set(readPages);

        // calculate currentPage

        this.currentPage.set(firstPage + readPages);

        // calculate daysLeft
        Long nowDateInMillis = new Date().getTime();
        Long deadlineDateInMillis = book.getDeadlineDate().getTime();
        Long daysBetween = daysBetween(nowDateInMillis, deadlineDateInMillis);
        this.daysLeft.set(daysBetween);

        // calculate readingTempo
        Long startDateInMilis = book.getStartDate().getTime();
        Long daysSinceStart = daysBetween(startDateInMilis, nowDateInMillis);
        Double tempo = ((double) readPages) / ((double) daysSinceStart);
        this.readingTempo.set(Math.round(tempo));

        // calculate hasGoodReadingTempo
        Boolean isReadingTempoGood = false;
        if ((tempo != 0) && ((totalPages - readPages) / tempo <= daysBetween))
            isReadingTempoGood = true;
        this.hasGoodReadingTempo.set(isReadingTempoGood);

        // calculate readingTempoToMakeItToDeadline
        if (!isReadingTempoGood) {
            Long tempoToMakeIt = (totalPages - readPages) / daysBetween;
            this.readingTempoToMakeIt.set(tempoToMakeIt);
        }

    }

    @Override
    public void onDataNotAvailable() {
        book.set(null);
        mIsDataLoading = false;
    }

    public void onRefresh() {
        if (book.get() != null) {
            start(book.get().getId());
        }
    }

    private void showSnackbarMessage(@StringRes Integer message) {
        mSnackbarText.setValue(message);
    }

    private Long daysBetween(Long date1, Long date2) {
        Long daysBetween = Math.round((double) (date2 - date1) / (1000 * 60 * 60 * 24));
        return daysBetween > 0 ? daysBetween : 1L; //prevent situation when now == startDate
    }
}
