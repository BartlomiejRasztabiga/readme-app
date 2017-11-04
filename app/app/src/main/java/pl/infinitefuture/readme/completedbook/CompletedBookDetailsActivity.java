package pl.infinitefuture.readme.completedbook;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ReadMeApplication;
import pl.infinitefuture.readme.ViewModelFactory;
import pl.infinitefuture.readme.addeditbook.AddEditBookActivity;
import pl.infinitefuture.readme.addeditbook.AddEditBookFragment;
import pl.infinitefuture.readme.sessions.SessionsActivity;
import pl.infinitefuture.readme.util.ActivityUtils;

import static pl.infinitefuture.readme.addeditbook.AddEditBookActivity.ADD_EDIT_RESULT_OK;
import static pl.infinitefuture.readme.bookdetail.BookDetailFragment.REQUEST_EDIT_BOOK;

/**
 * Displays book details screen.
 */
public class CompletedBookDetailsActivity extends AppCompatActivity implements CompletedBookDetailsNavigator {

    private static final String TAG = "CompletedBookDetailsActivity";

    public static final String EXTRA_BOOK_ID = "BOOK_ID";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private CompletedBookDetailViewModel mBookViewModel;

    private InterstitialAd mInterstitial;

    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completedbookdetail_act);

        setupToolbar();

        setupViewFragment();

        mBookViewModel = obtainViewModel(this);

        subscribeToNavigationChanges(mBookViewModel);

        setupAd();

        // Setup Analytics tracker
        ReadMeApplication application = (ReadMeApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mBookViewModel.getSetToolbarTitleCommand().observe(this, s -> {
            getSupportActionBar().setTitle(s);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Send screen name to Analytics
        mTracker.setScreenName("Book details");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void setupViewFragment() {
        CompletedBookDetailFragment completedBookDetailFragment = findOrCreateViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                completedBookDetailFragment, R.id.contentFrame);
    }

    private void setupAd() {
        // Initialise interstitial ad
        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(getString(R.string.onAddSessionInterstitial_id));
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Send event to Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Ads")
                        .setAction("onAddSession interstitial clicked")
                        .build());
            }

            @Override
            public void onAdLeftApplication() {
                // Send event to Analytics
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Ads")
                        .setAction("onAddSession interstitial clicked")
                        .build());
            }
        });

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("F259DB1215FFE47DFF8D24207A7A1B56") //Bartłomiej Rasztabiga genymotion
                .addTestDevice("862CC9D548D4515DF8A5FB779827E938") //Bartłomiej Rasztabiga redmi note 3
                .build();
        mInterstitial.loadAd(adRequest);
    }

    @NonNull
    private CompletedBookDetailFragment findOrCreateViewFragment() {
        // Get the requested book id
        Long bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0L);

        CompletedBookDetailFragment completedBookDetailFragment = (CompletedBookDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (completedBookDetailFragment == null) {
            completedBookDetailFragment = CompletedBookDetailFragment.newInstance(bookId);
        }
        return completedBookDetailFragment;
    }

    @NonNull
    public static CompletedBookDetailViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(CompletedBookDetailViewModel.class);
    }

    private void subscribeToNavigationChanges(CompletedBookDetailViewModel viewModel) {
        // The activity observes the navigation commands in the ViewModel
        viewModel.getEditBookCommand().observe(this, e -> CompletedBookDetailsActivity.this.onStartEditBook());
        viewModel.getDeleteBookCommand().observe(this, e -> CompletedBookDetailsActivity.this.onBookDeleted());
        viewModel.getOpenSessionsCommand().observe(this, e -> CompletedBookDetailsActivity.this.onOpenSessionsList());
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
    public void onOpenSessionsList() {
        Long bookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0L);
        Intent intent = new Intent(this, SessionsActivity.class);
        intent.putExtra(SessionsActivity.ARGUMENT_BOOK_ID, bookId);
        startActivity(intent);
    }

    private void showInterstitial() {
        // show ad
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet");
        }
    }
}
