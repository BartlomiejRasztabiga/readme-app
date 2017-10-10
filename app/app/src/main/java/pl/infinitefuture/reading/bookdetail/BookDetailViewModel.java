package pl.infinitefuture.reading.bookdetail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.StringRes;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.SingleLiveEvent;
import pl.infinitefuture.reading.SnackbarMessage;
import pl.infinitefuture.reading.books.BooksRepository;
import pl.infinitefuture.reading.books.persistence.Book;
import pl.infinitefuture.reading.books.persistence.BooksDataSource;

public class BookDetailViewModel extends AndroidViewModel implements BooksDataSource.GetBookCallback {

    public final ObservableField<Book> book = new ObservableField<>();

    public final ObservableBoolean completed = new ObservableBoolean();

    public final ObservableField<Long> pagesLeftToRead = new ObservableField<>();

    public final ObservableField<Long> daysLeft = new ObservableField<>();

    public final ObservableBoolean hasGoodReadingTempo = new ObservableBoolean();

    private final SingleLiveEvent<Void> mEditBookCommand = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mDeleteBookCommand = new SingleLiveEvent<>();

    private final BooksRepository mBooksRepository;

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private boolean mIsDataLoading;

    public BookDetailViewModel(Application context, BooksRepository booksRepository) {
        super(context);
        mBooksRepository = booksRepository;
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

    public SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    public SingleLiveEvent<Void> getEditBookCommand() {
        return mEditBookCommand;
    }

    public SingleLiveEvent<Void> getDeleteBookCommand() {
        return mDeleteBookCommand;
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
        }
    }

    public void setBook(Book book) {
        this.book.set(book);
        if (book != null) {
            completed.set(book.isCompleted());
        }
    }

    public boolean isDataAvailable() {
        return book.get() != null;
    }

    public boolean isDataLoading() {
        return mIsDataLoading;
    }

    @Override
    public void onBookLoaded(Book book) {
        setBook(book);
        mIsDataLoading = false;

        // calculate some data

        // calculate pagesLeftToRead
        Long totalPages = book.getTotalPages() != null ? book.getTotalPages() : 0L;
        Long readPages = book.getReadPages() != null ? book.getReadPages() : 0L;
        this.pagesLeftToRead.set(totalPages - readPages);

        // calculate daysLeft
        Long nowDateInMilis = new Date().getTime();
        Long deadlineDateInMilis = book.getDeadlineDate().getTime();
        Long daysBetween = daysBetween(nowDateInMilis, deadlineDateInMilis);
        this.daysLeft.set(daysBetween);

        //TODO Firstly calculate tempo

        // calculate hasGoodReadingTempo


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
        return Math.round((double) (date2 - date1) / (1000 * 60 * 60 * 24));
    }
}
