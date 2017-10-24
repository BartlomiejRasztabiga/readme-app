package pl.infinitefuture.readme.sessions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.databinding.SessionsFragBinding;
import pl.infinitefuture.readme.util.SnackbarUtils;

public class SessionsFragment extends Fragment {

    public static final String ARGUMENT_BOOK_ID = "BOOK_ID";

    private SessionsViewModel mSessionsViewModel;

    private SessionsFragBinding mSessionsFragBinding;

    public SessionsFragment() {
        // Requires empty public constructor
    }

    public static SessionsFragment newInstance() {
        return new SessionsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSessionsFragBinding = SessionsFragBinding.inflate(inflater, container, false);

        mSessionsViewModel = SessionsActivity.obtainViewModel(getActivity());

        mSessionsFragBinding.setViewmodel(mSessionsViewModel);

        setHasOptionsMenu(true);

        return mSessionsFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupSnackbar();

        setupRecyclerViewAdapter();

        loadData();
    }

    private void loadData() {
        if (getArguments() != null) {
            Long bookId = getArguments().getLong(ARGUMENT_BOOK_ID);
            if (!bookId.equals(0L)) {
                mSessionsViewModel.start(bookId);
            }
        }
    }

    private void setupSnackbar() {
        mSessionsViewModel.getSnackbarMessage()
                .observe(this, (SnackbarMessage.SnackbarObserver) snackbarMessageResourceId ->
                        SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId)));
    }

    private void setupRecyclerViewAdapter() {
        RecyclerView recyclerView = mSessionsFragBinding.sessionsList;

        RecyclerViewSessionsAdapter mAdapter = new RecyclerViewSessionsAdapter(
                new ArrayList<>(0)
        );

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.are_you_sure_to_delete);

                    builder.setPositiveButton(R.string.remove, (dialog, which) -> {
                        mAdapter.notifyItemRemoved(position);
                        mSessionsViewModel.removeSession(mAdapter.getItemForPosition(position)); //TODO Add callback, update book readPages
                        mAdapter.removeAtPosition(position);

                    }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                        mAdapter.notifyItemRemoved(position + 1);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                    }).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}
