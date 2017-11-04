package pl.infinitefuture.readme.addeditbook;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.SnackbarMessage;
import pl.infinitefuture.readme.databinding.AddbookFragBinding;
import pl.infinitefuture.readme.util.SnackbarUtils;

public class AddEditBookFragment extends Fragment {

    public static final String ARGUMENT_EDIT_BOOK_ID = "EDIT_BOOK_ID";

    private AddEditBookViewModel mViewModel;

    private AddbookFragBinding mViewDataBinding;

    public static AddEditBookFragment newInstance() {
        return new AddEditBookFragment();
    }

    public AddEditBookFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupFab();

        setupSnackbar();

        setupActionBar();

        loadData();
    }

    private void loadData() {
        // Add or edit an existing book?
        if (getArguments() != null) {
            Long editBookId = getArguments().getLong(ARGUMENT_EDIT_BOOK_ID);
            if (!editBookId.equals(0L)) {
                mViewModel.start(editBookId);
            }
        } else {
            mViewModel.start(null);
        }
    }

    public void showDatePickerDialog(View view) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), (datePicker, year, month, day) -> {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Calendar newDate = Calendar.getInstance();
            newDate.set(Calendar.YEAR, year);
            newDate.set(Calendar.MONTH, month);
            newDate.set(Calendar.DAY_OF_MONTH, day);

            mViewModel.setDate(sdf.format(newDate.getTime()), view);
        },
                currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.addbook_frag, container, false);
        if (mViewDataBinding == null) {
            mViewDataBinding = AddbookFragBinding.bind(root);
        }

        mViewModel = AddEditBookActivity.obtainViewModel(getActivity());

        mViewDataBinding.setViewmodel(mViewModel);

        setHasOptionsMenu(true);
        setRetainInstance(false);

        // set datepickers click listeners
        mViewDataBinding.getRoot().findViewById(R.id.add_book_start_date)
                .setOnFocusChangeListener(datepickerFocusChangeListener);

        mViewDataBinding.getRoot().findViewById(R.id.add_book_deadline_date)
                .setOnFocusChangeListener(datepickerFocusChangeListener);

        mViewDataBinding.getRoot().findViewById(R.id.add_book_start_date)
                .setOnClickListener(this::showDatePickerDialog);

        mViewDataBinding.getRoot().findViewById(R.id.add_book_deadline_date)
                .setOnClickListener(this::showDatePickerDialog);

        return mViewDataBinding.getRoot();
    }

    private View.OnFocusChangeListener datepickerFocusChangeListener = (view, hasFocus) -> {
        if (hasFocus) showDatePickerDialog(view);
    };

    private void setupSnackbar() {
        mViewModel.getSnackbarMessage().observe(this, (SnackbarMessage.SnackbarObserver)
                snackbarMessageResourceId -> SnackbarUtils.showSnackbar(getView(),
                        getString(snackbarMessageResourceId)));
    }

    private void setupFab() {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_book_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(v -> mViewModel.saveBook(getView()));
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        Long editBookId = getArguments().getLong(ARGUMENT_EDIT_BOOK_ID);
        if (getArguments() != null && !editBookId.equals(0L)) {
            actionBar.setTitle(R.string.edit_book);
        } else {
            actionBar.setTitle(R.string.add_book);
        }
    }
}
