package pl.infinitefuture.reading.bookdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.Toast;

import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.ViewModelFactory;
import pl.infinitefuture.reading.addeditbook.AddEditBookFragment;
import pl.infinitefuture.reading.addeditbook.AddEditBookActivity;
import pl.infinitefuture.reading.util.ActivityUtils;

import static pl.infinitefuture.reading.addeditbook.AddEditBookActivity.ADD_EDIT_RESULT_OK;
import static pl.infinitefuture.reading.bookdetail.BookDetailFragment.REQUEST_EDIT_BOOK;

/**
 * Displays book details screen.
 */
public class BookDetailActivity extends AppCompatActivity implements BookDetailNavigator{

    public static final String EXTRA_BOOK_ID = "BOOK_ID";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private BookDetailViewModel mBookViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail_act);

        setupToolbar();

        setupViewFragment();

        mBookViewModel = obtainViewModel(this);

        subscribeToNavigationChanges(mBookViewModel);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void setupViewFragment() {
        BookDetailFragment bookDetailFragment = findOrCreateViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                bookDetailFragment, R.id.contentFrame);
    }

    @NonNull
    private BookDetailFragment findOrCreateViewFragment() {
        // Get the requested book id
        Long bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0L);

        BookDetailFragment bookDetailFragment = (BookDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (bookDetailFragment == null) {
            bookDetailFragment = BookDetailFragment.newInstance(bookId);
        }
        return bookDetailFragment;
    }

    @NonNull
    public static BookDetailViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(BookDetailViewModel.class);
    }

    private void subscribeToNavigationChanges(BookDetailViewModel viewModel) {
        // The activity observes the navigation commands in the ViewModel
        viewModel.getEditBookCommand().observe(this, e -> BookDetailActivity.this.onStartEditBook());
        viewModel.getDeleteBookCommand().observe(this, e -> BookDetailActivity.this.onBookDeleted());
        viewModel.getAddSessionCommand().observe(this, e -> BookDetailActivity.this.onStartAddSession());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_BOOK) {
            // If the book was edited successfully, go back to the list.
            if (resultCode == ADD_EDIT_RESULT_OK) {
                // If the result comes from the add/edit screen, it's an edit.
                setResult(EDIT_RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBookDeleted() {
        setResult(DELETE_RESULT_OK);
        // If the book was deleted successfully, go back to the list.
        finish();
    }

    @Override
    public void onStartEditBook() {
        Long bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0L);
        Intent intent = new Intent(this, AddEditBookActivity.class);
        intent.putExtra(AddEditBookFragment.ARGUMENT_EDIT_BOOK_ID, bookId);
        startActivityForResult(intent, REQUEST_EDIT_BOOK);
    }

    @Override
    public void onStartAddSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.addsession_dialog, null))
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    // bla bla

                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                });
        builder.create().show();
    }
}
