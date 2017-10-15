package pl.infinitefuture.reading.books;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;
import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.ViewModelFactory;
import pl.infinitefuture.reading.addeditbook.AddEditBookActivity;
import pl.infinitefuture.reading.bookdetail.BookDetailActivity;
import pl.infinitefuture.reading.util.ActivityUtils;

public class BooksActivity extends AppCompatActivity implements BooksNavigator, BookItemNavigator {

    private BooksViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_act);

        // Initialize Fabric and AdMob
        Fabric.with(this, new Crashlytics(), new Answers());
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        setupToolbar();

        setupNavigation();

        setupViewFragment();

        mViewModel = obtainViewModel(this);

        // Subscribe to "open book" event
        mViewModel.getOpenBookEvent().observe(this, bookId -> {
            if (bookId != null) {
                openBookDetails(bookId);
            }
        });

        // Subscribe to "new task" event
        mViewModel.getNewBookEvent().observe(this, e -> addNewBook());
    }

    public static BooksViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(BooksViewModel.class);
    }

    private void setupViewFragment() {
        BooksFragment booksFragment =
                (BooksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (booksFragment == null) {
            // Create the fragment
            booksFragment = BooksFragment.newInstance();
            ActivityUtils.replaceFragmentInActivity(
                    getSupportFragmentManager(), booksFragment, R.id.contentFrame);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.bottom_navigation_books_item:
                            break;
                        case R.id.bottom_navigation_about_item:
                            // navigate to about activity
                            break;
                    }
                    return true;
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mViewModel.handleActivityResult(requestCode, resultCode);
    }

    @Override
    public void openBookDetails(Long bookId) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, bookId);
        startActivityForResult(intent, AddEditBookActivity.REQUEST_CODE);
    }

    @Override
    public void addNewBook() {
        Intent intent = new Intent(this, AddEditBookActivity.class);
        startActivityForResult(intent, AddEditBookActivity.REQUEST_CODE);
    }
}
