package pl.infinitefuture.readme.archives;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ScrollChildSwipeRefreshLayout;
import pl.infinitefuture.readme.ViewModelFactory;
import pl.infinitefuture.readme.books.BooksActivity;
import pl.infinitefuture.readme.books.BooksAdapter;
import pl.infinitefuture.readme.books.BooksViewModel;
import pl.infinitefuture.readme.databinding.BooksFragBinding;

public class ArchivesFragment extends Fragment {

    private BooksViewModel mArchivesViewModel;

    private BooksFragBinding mArchivesFragBinding;

    public ArchivesFragment() {
        // Requires empty public constructor
    }

    public static ArchivesFragment newInstance() {
        return new ArchivesFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mArchivesViewModel.start(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mArchivesFragBinding = BooksFragBinding.inflate(inflater, container, false);

        mArchivesViewModel = BooksActivity.obtainViewModel(getActivity());

        mArchivesFragBinding.setViewmodel(mArchivesViewModel);

        setHasOptionsMenu(true);

        return mArchivesFragBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListAdapter();

        setupRefreshLayout();
    }

    private void setupListAdapter() {
        ListView listView = mArchivesFragBinding.booksList;

        BooksAdapter mListAdapter = new BooksAdapter(
                new ArrayList<>(0),
                mArchivesViewModel
        );
        listView.setAdapter(mListAdapter);
    }

    private void setupRefreshLayout() {
        ListView listView = mArchivesFragBinding.booksList;
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout = mArchivesFragBinding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);
    }

}
