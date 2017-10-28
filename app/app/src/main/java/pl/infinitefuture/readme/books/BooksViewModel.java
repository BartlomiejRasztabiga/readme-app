package pl.infinitefuture.readme.books;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SingleLiveEvent;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.addeditbook.AddEditBookActivity;
import pl.infinitefuture.readme.bookdetail.BookDetailActivity;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.books.persistence.BooksDataSource;

/**
 * Exposes the data to be used in the book list screen.
 * <p>
 * {@link BaseObservable} implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a {@link Bindable} annotation to the property's
 * getter method.
 */
public class BooksViewModel extends AndroidViewModel {

    // These observable fields will update Views automatically
    public final ObservableList<Book> items = new ObservableArrayList<>();

    public final ObservableBoolean dataLoading = new ObservableBoolean(false);

    public final ObservableBoolean empty = new ObservableBoolean(false);

    private final SnackbarMessage mSnackbarText = new SnackbarMessage();

    private final BooksRepository mBooksRepository;

    private final ObservableBoolean mIsDataLoadingError = new ObservableBoolean(false);

    private final ObservableBoolean onlyCompleted = new ObservableBoolean(false);

    private final SingleLiveEvent<Book> mOpenBookEvent = new SingleLiveEvent<>();

    private final SingleLiveEvent<Void> mNewBookEvent = new SingleLiveEvent<>();

    public BooksViewModel(
            Application context,
            BooksRepository repository) {
        super(context);
        mBooksRepository = repository;
    }

    public void start(boolean onlyCompleted) {
        this.onlyCompleted.set(onlyCompleted);
        loadBooks(false);
    }

    public void loadBooks() {
        loadBooks(true);
    }

    SnackbarMessage getSnackbarMessage() {
        return mSnackbarText;
    }

    SingleLiveEvent<Book> getOpenBookEvent() {
        return mOpenBookEvent;
    }

    SingleLiveEvent<Void> getNewBookEvent() {
        return mNewBookEvent;
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    public void addNewBook() {
        mNewBookEvent.call();
    }

    void handleActivityResult(int requestCode, int resultCode) {
        if (AddEditBookActivity.REQUEST_CODE == requestCode) {
            switch (resultCode) {
                case BookDetailActivity.EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_saved_book_message);
                    break;
                case AddEditBookActivity.ADD_EDIT_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_added_book_message);
                    break;
                case BookDetailActivity.DELETE_RESULT_OK:
                    mSnackbarText.setValue(R.string.successfully_deleted_book_message);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link BooksDataSource}
     */
    private void loadBooks(boolean forceUpdate) {
        if (forceUpdate) {
            mBooksRepository.refreshBooks();
        }

        dataLoading.set(true);

        if (onlyCompleted.get()) {
            loadCompletedBooks();
        } else {
            loadActiveBooks();
        }
    }

    private void loadActiveBooks() {
        mBooksRepository.getBooks(new BooksDataSource.LoadBooksCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                mIsDataLoadingError.set(false);
                dataLoading.set(false);

                items.clear();
                items.addAll(books);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                dataLoading.set(false);
                mIsDataLoadingError.set(true);
                mSnackbarText.setValue(R.string.no_internet);
            }
        });
    }

    private void loadCompletedBooks() {
        mBooksRepository.getCompletedBooks(new BooksDataSource.LoadBooksCallback() {
            @Override
            public void onBooksLoaded(List<Book> books) {
                mIsDataLoadingError.set(false);
                dataLoading.set(false);

                items.clear();
                items.addAll(books);
                empty.set(items.isEmpty());
            }

            @Override
            public void onDataNotAvailable() {
                dataLoading.set(false);
                mIsDataLoadingError.set(true);
                mSnackbarText.setValue(R.string.no_internet);
            }
        });
    }

    public ObservableBoolean getOnlyCompleted() {
        return onlyCompleted;
    }
}
