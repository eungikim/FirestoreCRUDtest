package kr.eungi.firestorecrudtest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.eungi.firestorecrudtest.db.NameRepository;

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.TextViewHolder> {

    private NameRepository repo;
    private OnListItemClickListener mListItemClickListener;

    class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.item_name_text_view);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListItemClickListener != null) {
                    mListItemClickListener.onListItemClick(itemView, position);
                }
            });
        }

        private void bindViewHolder(String name) {
            mNameTextView.setText(name);
        }

    }

    NameAdapter() {
        this.repo = NameRepository.getInstance();
    }

    void setListItemClickListener(OnListItemClickListener listener) {
        this.mListItemClickListener = listener;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_name, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        holder.bindViewHolder(repo.getNameList().get(position).getName());

    }

    @Override
    public int getItemCount() {
        return repo.getNameList().size();
    }


    interface OnListItemClickListener {
        void onListItemClick(View view, int position);
    }

}
