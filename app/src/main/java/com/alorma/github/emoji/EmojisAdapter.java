package com.alorma.github.emoji;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public EmojisAdapter() {
        emojis = new ArrayList<>();
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EmojiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.emoji_row, parent, false));
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {
        ImageLoader.getInstance().displayImage(emojis.get(position).getValue(), holder.icon);
    }

    @Override
    public int getItemCount() {
        return emojis.size();
    }

    public void addAll(Collection<Emoji> emojis) {
        this.emojis.addAll(emojis);
        notifyDataSetChanged();
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder{
        private final ImageView icon;

        public EmojiViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
