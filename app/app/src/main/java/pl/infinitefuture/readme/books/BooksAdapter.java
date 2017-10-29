package pl.infinitefuture.readme.books;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;

import java.util.Date;
import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.databinding.BookCompletedItemBinding;
import pl.infinitefuture.readme.databinding.BookItemBinding;
import pl.infinitefuture.readme.util.MaterialColors;

public class BooksAdapter extends BaseAdapter {

    private final BooksViewModel mBooksViewModel;

    private List<Book> mBooks;

    public BooksAdapter(List<Book> books, BooksViewModel booksViewModel) {
        mBooksViewModel = booksViewModel;
        setList(books);
    }

    public void replaceData(List<Book> books) {
        setList(books);
    }

    @Override
    public int getCount() {
        return mBooks != null ? mBooks.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (mBooks.get(position).isCompleted()) {
            return getViewForCompletedBook(position, view, viewGroup);
        } else {
            return getViewForActiveBook(position, view, viewGroup);
        }
    }

    private View getViewForActiveBook(int position, View view, ViewGroup viewGroup) {
        BookItemBinding binding;
        // Inflate
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        // Create the binding
        binding = BookItemBinding.inflate(inflater, viewGroup, false);

        BookItemUserActionsListener userActionsListener = book -> mBooksViewModel.getOpenBookEvent().setValue(book);

        binding.setBook(mBooks.get(position));

        binding.setListener(userActionsListener);

        binding.executePendingBindings();

        // Set first letter view
        Book book = binding.getBook();
        RoundedLetterView roundedLetterView = binding.getRoot().findViewById(R.id.icon);
        roundedLetterView.setTitleText(book.getFirstTitleLetter());

        // Set random color
        roundedLetterView.setBackgroundColor(book.getIconColor());

        // Set date color depending on tempo
        Long firstPage = book.getFirstPage() != null ? book.getFirstPage() : 0L;
        Long lastPage = book.getLastPage() != null ? book.getLastPage() : 0L;
        Long readPages = book.getReadPages() != null ? book.getReadPages() : 0L;
        Long totalPages = lastPage - firstPage;

        Long nowDateInMillis = new Date().getTime();
        Long startDateInMilis = book.getStartDate().getTime();
        Long deadlineDateInMillis = book.getDeadlineDate().getTime();

        Double daysBetween = daysBetween(nowDateInMillis, deadlineDateInMillis);
        Double daysSinceStart = daysBetween(startDateInMilis, nowDateInMillis);
        Double tempo = ((double) readPages / daysSinceStart);

        Boolean isReadingTempoGood = false;
        if ((tempo != 0) && ((totalPages - readPages) / tempo <= daysBetween))
            isReadingTempoGood = true;

        TextView deadlineDate = binding.getRoot().findViewById(R.id.deadline_date);
        deadlineDate.setTextColor(Color.parseColor(isReadingTempoGood ? MaterialColors.DATE_GREEN : MaterialColors.DATE_RED));

        return binding.getRoot();
    }

    private View getViewForCompletedBook(int position, View view, ViewGroup viewGroup) {
        BookCompletedItemBinding binding;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        binding = BookCompletedItemBinding.inflate(inflater, viewGroup, false);

        BookItemUserActionsListener userActionsListener = book -> mBooksViewModel.getOpenBookEvent().setValue(book);
        binding.setBook(mBooks.get(position));
        binding.setListener(userActionsListener);
        binding.executePendingBindings();

        // Set first letter view
        Book book = binding.getBook();
        RoundedLetterView roundedLetterView = binding.getRoot().findViewById(R.id.icon);
        roundedLetterView.setTitleText(book.getFirstTitleLetter());

        // Set random color
        roundedLetterView.setBackgroundColor(book.getIconColor());

        return binding.getRoot();
    }

    private void setList(List<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    private Double daysBetween(Long date1, Long date2) {
        Double daysBetween = (double) (date2 - date1) / (1000 * 60 * 60 * 24);
        return daysBetween > 0 ? daysBetween : 1L; //prevent situation when now == startDate
    }

}
