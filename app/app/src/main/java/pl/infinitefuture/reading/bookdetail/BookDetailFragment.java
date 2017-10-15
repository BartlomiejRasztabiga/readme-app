package pl.infinitefuture.reading.bookdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.SnackbarMessage;
import pl.infinitefuture.reading.addeditbook.BookDetailUserActionsListener;
import pl.infinitefuture.reading.databinding.BookdetailFragBinding;
import pl.infinitefuture.reading.sessions.ReadingSessionsAdapter;
import pl.infinitefuture.reading.sessions.ReadingSessionsListBindings;
import pl.infinitefuture.reading.util.SnackbarUtils;

public class BookDetailFragment extends Fragment {

    public static final String ARGUMENT_BOOK_ID = "BOOK_ID";

    public static final int REQUEST_EDIT_BOOK = 1;

    private BookDetailViewModel mViewModel;

    private BookdetailFragBinding mBookDetailsFragBinding;

    public static BookDetailFragment newInstance(Long bookId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_BOOK_ID, bookId);
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupToolbar();
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_session);

        fab.setOnClickListener(v -> mViewModel.addSession());
    }

    private void setupSnackbar() {
        mViewModel.getSnackbarMessage().observe(this,
                (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId ->
                        SnackbarUtils.showSnackbar(getView(),
                                getString(snackbarMessageResourceId)));
    }

    private void setupToolbar() {
        mViewModel.getSetToolbarTitleCommand().observe(this, title ->
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title));
    }

    @Override
    public void onResume() {
        super.onResume();
        Long bookId = getArguments().getLong(ARGUMENT_BOOK_ID);
        if (bookId != 0L) {
            mViewModel.start(getArguments().getLong(ARGUMENT_BOOK_ID));
            //TODO set toolbar title
        } // Else I don't know...
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bookdetail_frag, container, false);

        BookdetailFragBinding viewDataBinding = BookdetailFragBinding.bind(view);

        mViewModel = BookDetailActivity.obtainViewModel(getActivity());

        viewDataBinding.setViewmodel(mViewModel);

        BookDetailUserActionsListener actionsListener = getBookDetailUserActionsListener();

        viewDataBinding.setListener(actionsListener);

        setHasOptionsMenu(true);

        // setupReadingSessionsListAdapter
        ListView listView = viewDataBinding.sessionsList;

        ReadingSessionsAdapter mListAdapter = new ReadingSessionsAdapter(new ArrayList<>(0));
        listView.setAdapter(mListAdapter);

        return view;
    }

    private BookDetailUserActionsListener getBookDetailUserActionsListener() {
        return v -> mViewModel.setCompleted(((CheckBox) v).isChecked());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bookdetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_delete) {
            mViewModel.deleteBook();
            return true;
        } else if (i == R.id.menu_edit) {
            mViewModel.editBook();
            return true;
        }
        return false;
    }
}
