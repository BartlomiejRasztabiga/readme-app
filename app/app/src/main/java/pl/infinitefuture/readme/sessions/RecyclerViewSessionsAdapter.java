package pl.infinitefuture.readme.sessions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.databinding.SessionFullItemBinding;
import pl.infinitefuture.readme.sessions.persistence.ReadingSession;

public class RecyclerViewSessionsAdapter
        extends RecyclerView.Adapter<RecyclerViewSessionsAdapter.ViewHolder> {

    private List<ReadingSession> mSessions;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final SessionFullItemBinding binding;

        public ViewHolder(SessionFullItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(ReadingSession session) {
            binding.setSession(session);
            binding.executePendingBindings();
        }

    }

    public RecyclerViewSessionsAdapter(List<ReadingSession> sessions) {
        mSessions = sessions;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        SessionFullItemBinding sessionFullItemBinding =
                SessionFullItemBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(sessionFullItemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReadingSession session = getItemForPosition(position);
        holder.bind(session);
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    public void replaceData(List<ReadingSession> items) {
        mSessions = items;
        notifyDataSetChanged();
    }

    public ReadingSession getItemForPosition(int position) {
        return mSessions.get(position);
    }

    public void removeAtPosition(int position) {
        mSessions.remove(position);
    }
}
