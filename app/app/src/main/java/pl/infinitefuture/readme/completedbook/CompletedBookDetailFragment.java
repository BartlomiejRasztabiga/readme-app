package pl.infinitefuture.readme.completedbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ReadMeApplication;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.databinding.BookdetailFragBinding;
import pl.infinitefuture.readme.databinding.CompletedbookdetailFragBinding;
import pl.infinitefuture.readme.sessions.ReadingSessionsAdapter;
import pl.infinitefuture.readme.util.SnackbarUtils;

public class CompletedBookDetailFragment extends Fragment {

    public static final String ARGUMENT_BOOK_ID = "BOOK_ID";

    public static final int REQUEST_EDIT_BOOK = 1;

    private CompletedBookDetailViewModel mViewModel;

    private CompletedbookdetailFragBinding mBookDetailsFragBinding;

    private Tracker mTracker;

    public static CompletedBookDetailFragment newInstance(Long bookId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_BOOK_ID, bookId);
        CompletedBookDetailFragment fragment = new CompletedBookDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        // Setup Analytics tracker
        ReadMeApplication application = (ReadMeApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarMessage().observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId ->
                        SnackbarUtils.showSnackbar(getView(),
                                getString(snackbarMessageResourceId)));
    }

    @Override
    public void onResume() {
        super.onResume();
        Long bookId = getArguments().getLong(ARGUMENT_BOOK_ID);
        if (bookId != 0L) {
            mViewModel.start(getArguments().getLong(ARGUMENT_BOOK_ID));
        } // Else I don't know...
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completedbookdetail_frag, container, false);

        CompletedbookdetailFragBinding viewDataBinding = CompletedbookdetailFragBinding.bind(view);

        mViewModel = CompletedBookDetailsActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);

        // setupReadingSessionsListAdapter
        ListView listView = viewDataBinding.sessionsList;

        ReadingSessionsAdapter mListAdapter = new ReadingSessionsAdapter(new ArrayList<>(0));
        listView.setAdapter(mListAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bookdetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_delete) {
            showDeleteBookDialog();
            return true;
        } else if (i == R.id.menu_edit) {
            mViewModel.editBook();
            sendEditBookEvent();
            return true;
        }
        return false;
    }

    private void showDeleteBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.are_you_sure_to_delete);

        builder.setPositiveButton(R.string.remove, (dialog, which) -> {
            mViewModel.deleteBook();
            sendDeleteBookEvent();

        }).setNegativeButton(R.string.cancel, (dialog, which) -> {
            // do nothing
        }).show();
    }

    private void sendDeleteBookEvent() {
        // Send event to Analytics
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Delete book")
                .build());
    }

    private void sendEditBookEvent() {
        // Send event to Analytics
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Edit book")
                .build());
    }
}
