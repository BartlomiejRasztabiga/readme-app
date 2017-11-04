package pl.infinitefuture.readme.archives;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ScrollChildSwipeRefreshLayout;
import pl.infinitefuture.readme.books.BooksActivity;
import pl.infinitefuture.readme.books.BooksAdapter;
import pl.infinitefuture.readme.books.BooksViewModel;
import pl.infinitefuture.readme.databinding.BooksFragBinding;

public class ArchivesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private BooksViewModel mArchivesViewModel;

    private BooksFragBinding mArchivesFragBinding;

    private BooksAdapter mAdapter;

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

        mAdapter = new BooksAdapter(
                new ArrayList<>(0),
                mArchivesViewModel
        );
        listView.setAdapter(mAdapter);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.archives_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.filter(newText);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.filter(query);
        return false;
    }
}
