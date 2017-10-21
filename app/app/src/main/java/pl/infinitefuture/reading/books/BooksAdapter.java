package pl.infinitefuture.reading.books;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;

import java.util.Date;
import java.util.List;
import java.util.Random;

import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.books.persistence.Book;
import pl.infinitefuture.reading.databinding.BookItemBinding;
import pl.infinitefuture.reading.util.MaterialColors;

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
        BookItemBinding binding;
        if (view == null) {
            // Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            // Create the binding
            binding = BookItemBinding.inflate(inflater, viewGroup, false);
        } else {
            // Recycling view
            binding = DataBindingUtil.getBinding(view);
        }

        BookItemUserActionsListener userActionsListener = book -> mBooksViewModel.getOpenBookEvent().setValue(book.getId());

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
        
        Long daysBetween = daysBetween(nowDateInMillis, deadlineDateInMillis);
        Long daysSinceStart = daysBetween(startDateInMilis, nowDateInMillis);
        Double tempo = ((double) readPages) / ((double) daysSinceStart);

        Boolean isReadingTempoGood = false;
        if ((tempo != 0) && ((totalPages - readPages) / tempo <= daysBetween))
            isReadingTempoGood = true;

        TextView deadlineDate = binding.getRoot().findViewById(R.id.deadline_date);
        deadlineDate.setTextColor(Color.parseColor(isReadingTempoGood ? MaterialColors.DATE_GREEN : MaterialColors.DATE_RED));

        return binding.getRoot();
    }

    private void setList(List<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    private Long daysBetween(Long date1, Long date2) {
        Long daysBetween = Math.round((double) (date2 - date1) / (1000 * 60 * 60 * 24));
        return daysBetween > 0 ? daysBetween : 1L; //prevent situation when now == startDate
    }

}
