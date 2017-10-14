package pl.infinitefuture.reading.sessions;

import android.databinding.DataBindingUtil;
import android.support.annotation.RequiresPermission;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.infinitefuture.reading.bookdetail.BookDetailViewModel;
import pl.infinitefuture.reading.databinding.SessionItemBinding;
import pl.infinitefuture.reading.sessions.persistence.ReadingSession;

public class ReadingSessionsAdapter extends BaseAdapter {

    private List<ReadingSession> mSessions;

    public ReadingSessionsAdapter(List<ReadingSession> sessions) {
        setList(sessions);
    }

    public void replaceData(List<ReadingSession> sessions) {
        setList(sessions);
    }

    @Override
    public int getCount() {
        return mSessions != null ? mSessions.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mSessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        SessionItemBinding binding;
        if (view == null) {
            // Inflate
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            // Create the binding
            binding = SessionItemBinding.inflate(inflater, viewGroup, false);
        } else {
            // Recycling view
            binding = DataBindingUtil.getBinding(view);
        }

        binding.setSession(mSessions.get(position));

        binding.executePendingBindings();

        return binding.getRoot();
    }

    private void setList(List<ReadingSession> sessions) {
        mSessions = Lists.reverse(sessions); //reverse list to have newest session on top
        if (mSessions.size() > 3) {
            mSessions = mSessions.subList(0, 3); //keep only 3 items
        }

        notifyDataSetChanged();
    }
}
