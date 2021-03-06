package pl.infinitefuture.readme.addeditbook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;

import com.google.common.base.Strings;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Date;

import pl.infinitefuture.readme.EditTextBindingAdapters;
import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SingleLiveEvent;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.books.BooksRepository;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.books.persistence.BooksDataSource;

public class AddEditBookViewModel extends AndroidViewModel implements BooksDataSource.GetBookCallback {

    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<Long> firstPage = new ObservableField<>();

    public final ObservableField<Long> lastPage = new ObservableField<>();

    public final ObservableField<String> startDate = new ObservableField<>();

    public final ObservableField<String> deadlineDate = new ObservableField<>();

    public final ObservableField<Long> readPages = new ObservableField<>();

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
        firstPage.set(book.getFirstPage());
        lastPage.set(book.getLastPage());
        readPages.set(book.getReadPages());
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
    void saveBook(View v) {
        TextInputLayout bookTitleLayout = v.findViewById(R.id.add_book_title_layout);
        TextInputLayout bookFirstPageLayout = v.findViewById(R.id.add_book_firstpage_layout);
        TextInputLayout bookLastPageLayout = v.findViewById(R.id.add_book_pages_layout);
        TextInputLayout bookStartDateLayout = v.findViewById(R.id.add_book_start_date_layout);
        TextInputLayout bookEndDateLayout = v.findViewById(R.id.add_book_deadline_date_layout);

        bookTitleLayout.setErrorEnabled(false);
        bookFirstPageLayout.setErrorEnabled(false);
        bookLastPageLayout.setErrorEnabled(false);
        bookStartDateLayout.setErrorEnabled(false);
        bookEndDateLayout.setErrorEnabled(false);

        boolean errorInForm = false;

        try {
            Date bookStartDate = EditTextBindingAdapters.strToDate(this.startDate.get());
            Date bookDeadlineDate = EditTextBindingAdapters.strToDate(this.deadlineDate.get());

            // check if deadlineDate is before startDate
            if (bookStartDate.getTime() >= bookDeadlineDate.getTime()) {
                bookEndDateLayout.setError(getApplication().getString(R.string.end_before_start_date_message));
                errorInForm = true;
            }

            // check if title is empty
            if (Strings.isNullOrEmpty(this.title.get())) {
                bookTitleLayout.setError(getApplication().getString(R.string.field_empty));
                errorInForm = true;
            }

            //check if startPage and lastPage are empty
            if (this.firstPage.get() == null || this.lastPage.get() == null) {
                bookFirstPageLayout.setError(getApplication().getString(R.string.field_empty));
                bookLastPageLayout.setError(getApplication().getString(R.string.field_empty));
                return;
            }

            // check if lastPage is before startPage
            if (this.lastPage.get() <= this.firstPage.get()) {
                bookLastPageLayout.setError(getApplication().getString(R.string.last_page_before_first_page));
                errorInForm = true;
            }

            Book book = new Book(title.get(), firstPage.get(), lastPage.get(), bookStartDate, bookDeadlineDate);

            if (errorInForm) return;

            if (!mIsNewBook && mBookId != null) {
                book = new Book(mBookId, title.get(), firstPage.get(), lastPage.get(),
                        readPages.get(), bookStartDate, bookDeadlineDate, mBookCompleted,
                        book.getIconColor(), book.getCompleteDate()); //TODO Fix getting color
                updateBook(book);
            } else {
                saveBook(book);
            }
        } catch (ParseException | InvalidDateException e) {
            bookStartDateLayout.setError(getApplication().getString(R.string.invalid_date_message));
            bookEndDateLayout.setError(getApplication().getString(R.string.invalid_date_message));
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
