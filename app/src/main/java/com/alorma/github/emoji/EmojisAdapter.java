package com.alorma.github.emoji;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisAdapter extends RecyclerView.Adapter<EmojisAdapter.EmojiViewHolder> {

    private final List<Emoji> emojis;
    private OnEmojiSelectedListener onEmojiSelectedListener;

    public EmojisAdapter() {
        emojis = new ArrayList<>();
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmojiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_row, parent, false));
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {
        Emoji emoji = emojis.get(position);
        ImageLoader.getInstance().displayImage(emoji.getValue(), holder.icon);
        holder.text.setText(":" + emoji.getKey() + ":");
    }

    @Override
    public int getItemCount() {
        return emojis.size();
    }

    public void addAll(Collection<Emoji> emojis) {
        this.emojis.addAll(emojis);
        notifyDataSetChanged();
    }

    public void clear() {
        emojis.clear();
        notifyDataSetChanged();
    }

    public void setOnEmojiSelectedListener(OnEmojiSelectedListener onEmojiSelectedListener) {
        this.onEmojiSelectedListener = onEmojiSelectedListener;
    }

    public interface OnEmojiSelectedListener {
        void onEmojiSelected(Emoji emoji);
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView text;

        public EmojiViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            text = (TextView) itemView.findViewById(R.id.text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onEmojiSelectedListener != null) {
                onEmojiSelectedListener.onEmojiSelected(emojis.get(getAdapterPosition()));
            }
        }
    }
}
