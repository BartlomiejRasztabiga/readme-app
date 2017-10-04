package pl.infinitefuture.reading.books;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.pavlospt.roundedletterview.RoundedLetterView;

import java.util.List;
import java.util.Random;

import pl.infinitefuture.reading.R;
import pl.infinitefuture.reading.books.persistence.Book;
import pl.infinitefuture.reading.databinding.BookItemBinding;

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

        return binding.getRoot();
    }

    private void setList(List<Book> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

}
