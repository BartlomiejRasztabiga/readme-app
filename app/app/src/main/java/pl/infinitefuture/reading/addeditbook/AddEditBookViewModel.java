package pl.infinitefuture.reading.addeditbook;

import android.app.Application;
import android.app.DatePickerDialog;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.infinitefuture.reading.EditTextBindingAdapters;
import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.SingleLiveEvent;
import pl.infinitefuture.reading.SnackbarMessage;
import pl.infinitefuture.reading.books.BooksRepository;
import pl.infinitefuture.reading.books.persistence.Book;
import pl.infinitefuture.reading.books.persistence.BooksDataSource;

public class AddEditBookViewModel extends AndroidViewModel implements BooksDataSource.GetBookCallback {

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<Long> totalPages = new ObservableField<>();

    public final ObservableField<String> startDate = new ObservableField<>();

    public final ObservableField<String> deadlineDate = new ObservableField<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    @VisibleForTesting
    final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final SingleLiveEvent<Void> mBookUpdated = new SingleLiveEvent<>();

    private final BooksRepository mBooksRepository;

    private final WeakReference<Application> mContext;

    @Nullable
    private Long mBookId;

    @VisibleForTesting
    boolean mIsNewBook;

    @VisibleForTesting
    boolean mIsDataLoaded = false;

    @VisibleForTesting
    boolean mBookCompleted = false;

    public AddEditBookViewModel(Application context,
                                BooksRepository booksRepository) {
        super(context);
        mBooksRepository = booksRepository;
        mContext = new WeakReference<>(context);
    }

    public void start(Long bookId) {
        if (dataLoading.get()) {
            // Already loading, ignore
            return;
        }
        mBookId = bookId;
        if (bookId == null) {
            // No need to populate, it's a new task
            mIsNewBook = true;
            return;
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data
            return;
        }
        mIsNewBook = false;
        dataLoading.set(true);

        mBooksRepository.getBook(bookId, this);
    }

    @Override
    public void onBookLoaded(Book book) {
        title.set(book.getTitle());
        totalPages.set(book.getTotalPages());
        startDate.set(EditTextBindingAdapters.dateToStr(book.getStartDate()));
        deadlineDate.set(EditTextBindingAdapters.dateToStr(book.getDeadlineDate()));
        mBookCompleted = book.isCompleted();
        dataLoading.set(false);
        mIsDataLoaded = true;

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    @Override
    public void onDataNotAvailable() {
        dataLoading.set(false);
    }

    // Called when clicking on fab.
    void saveBook() {
        try {
            Book book = new Book(title.get(), totalPages.get(),
                    EditTextBindingAdapters.strToDate(startDate.get()),
                    EditTextBindingAdapters.strToDate(deadlineDate.get()));
            if (book.isEmpty()) {
                mSnackbarText.setValue(R.string.empty_book_message);
                return;
            }
            if (!mIsNewBook && mBookId != null) {
                book = new Book(mBookId, title.get(), totalPages.get(),
                        EditTextBindingAdapters.strToDate(startDate.get()),
                        EditTextBindingAdapters.strToDate(deadlineDate.get()), mBookCompleted,
                        book.getIconColor()); //TODO Fix getting color
                updateBook(book);
            } else {
                saveBook(book);
            }
        } catch (ParseException | InvalidDateException e) {
            mSnackbarText.setValue(R.string.invalid_date_message);
        }

    }

    void setDate(String date, View view) {
        if (view.getId() == R.id.add_book_start_date) {
            this.startDate.set(date);
        } else if (view.getId() == R.id.add_book_deadline_date) {
            this.deadlineDate.set(date);
        }
    }

    SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    SingleLiveEvent<Void> getBookUpdatedEvent() {
        return mBookUpdated;
    }

    private void updateBook(Book book) {
        mBooksRepository.updateBook(book);
        mBookUpdated.call();

        // Refresh to keep data consistent
        mBooksRepository.refreshBooks();
    }

    private void saveBook(Book book) {
        Log.d("AddEditBookViewModel", "Saving book: " + book.toString());
        mBooksRepository.saveBook(book, new BooksDataSource.SaveBookCallback() {
            @Override
            public void onBookSaved(Long bookId) {
                mBookUpdated.call();
            }

            @Override
            public void onDataNotAvailable() {
                getSnackbarMessage().setValue(R.string.error_save_book);
            }
        });
    }
}
