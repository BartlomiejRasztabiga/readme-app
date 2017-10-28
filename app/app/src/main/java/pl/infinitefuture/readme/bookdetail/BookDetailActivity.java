package pl.infinitefuture.readme.bookdetail;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pl.infinitefuture.readme.EditTextBindingAdapters;
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
public class BookDetailActivity extends AppCompatActivity implements BookDetailNavigator {

    private static final String TAG = "BookDetailActivity";

    public static final String EXTRA_BOOK_ID = "BOOK_ID";

    public static final int DELETE_RESULT_OK = RESULT_FIRST_USER + 2;

    public static final int EDIT_RESULT_OK = RESULT_FIRST_USER + 3;

    private BookDetailViewModel mBookViewModel;

    private InterstitialAd mInterstitial;

    private Tracker mTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail_act);

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
        BookDetailFragment bookDetailFragment = findOrCreateViewFragment();

        ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(),
                bookDetailFragment, R.id.contentFrame);
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
        viewModel.getOpenSessionsCommand().observe(this, e -> BookDetailActivity.this.onOpenSessionsList());
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

    @Override
    public void onStartAddSession() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        Calendar newDate = Calendar.getInstance();
        View dialogView = inflater.inflate(R.layout.addsession_dialog, null);
        TextInputEditText page = dialogView.findViewById(R.id.add_session_current_page);

        // set default date to now
        ((TextInputEditText)dialogView.findViewById(R.id.add_session_date))
                .setText(EditTextBindingAdapters.dateToStr(newDate.getTime()));

        dialogView.findViewById(R.id.add_session_date).setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog(dialogView, newDate);
            }
        });

        dialogView.findViewById(R.id.add_session_date).setOnClickListener(view -> {
           showDatePickerDialog(dialogView, newDate);
        });

        builder.setView(dialogView)
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    // ignore
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    // ignore
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            Boolean wantToCloseDialog = true;
            if (page.getText() == null || page.getText().toString().equals("")
                    || newDate.getTime() == null) {
                Toast.makeText(this, R.string.errors_in_form, Toast.LENGTH_SHORT).show();
                wantToCloseDialog = false;
            } else if (newDate.getTime().getTime() > new Date().getTime()) {
                Toast.makeText(this, R.string.session_date_after_now, Toast.LENGTH_SHORT).show();
                wantToCloseDialog = false;
            } else if (newDate.getTime().getTime() < mBookViewModel.book.get().getStartDate().getTime()) {
                Toast.makeText(this, R.string.session_date_before_start_date, Toast.LENGTH_SHORT).show();
                wantToCloseDialog = false;
            }
            if (wantToCloseDialog) {
                //show ad
                showInterstitial();

                Long currentPage = Long.valueOf(page.getText().toString());
                mBookViewModel.addReadingSession(currentPage, newDate.getTime());
                dialog.dismiss();
            }
        });

    }

    private void showInterstitial() {
        // show ad
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
        } else {
            Log.d(TAG, "The interstitial wasn't loaded yet");
        }
    }

    private void showDatePickerDialog(View dialogView, Calendar newDate) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (datePicker, year, month, day) -> {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            newDate.set(Calendar.YEAR, year);
            newDate.set(Calendar.MONTH, month);
            newDate.set(Calendar.DAY_OF_MONTH, day);

            // set edittext value to chosen date
            ((TextInputEditText) dialogView.findViewById(R.id.add_session_date))
                    .setText(sdf.format(newDate.getTime()));
        },
                currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }


}
