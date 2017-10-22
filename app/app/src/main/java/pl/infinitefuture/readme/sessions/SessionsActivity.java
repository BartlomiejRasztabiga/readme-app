package pl.infinitefuture.readme.sessions;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ReadMeApplication;
import pl.infinitefuture.readme.ViewModelFactory;
import pl.infinitefuture.readme.addeditbook.AddEditBookFragment;
import pl.infinitefuture.readme.books.BooksFragment;
import pl.infinitefuture.readme.books.BooksViewModel;
import pl.infinitefuture.readme.util.ActivityUtils;

public class SessionsActivity extends AppCompatActivity {

    public static final String ARGUMENT_BOOK_ID = "BOOK_ID";

    private SessionsViewModel mSessionsViewModel;

    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sessions_act);

        setupToolbar();

        setupViewFragment();

        mSessionsViewModel = obtainViewModel(this);

        // Setup Analytics tracker
        ReadMeApplication application = (ReadMeApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Send screen name to Analytics
        mTracker.setScreenName("Sessions full list");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static SessionsViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(SessionsViewModel.class);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(R.string.sessions_list);
    }

    private void setupViewFragment() {
        SessionsFragment sessionsFragment = obtainViewFragment();
        ActivityUtils.replaceFragmentInActivity(
                getSupportFragmentManager(), sessionsFragment, R.id.contentFrame);
    }

    @NonNull
    private SessionsFragment obtainViewFragment() {
        // View Fragment
        SessionsFragment sessionsFragment = (SessionsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (sessionsFragment == null) {
            sessionsFragment = SessionsFragment.newInstance();

            // Send the task ID to the fragment
            Bundle bundle = new Bundle();
            bundle.putLong(SessionsFragment.ARGUMENT_BOOK_ID,
                    getIntent().getLongExtra(SessionsFragment.ARGUMENT_BOOK_ID, 0L));
            sessionsFragment.setArguments(bundle);
        }
        return sessionsFragment;
    }
}
