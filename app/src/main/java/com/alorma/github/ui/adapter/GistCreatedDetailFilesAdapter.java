package com.alorma.github.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistCreatedDetailFilesAdapter extends RecyclerArrayAdapter<GistFile, GistCreatedDetailFilesAdapter.ViewHolder> {

    private GistCreateAdapterListener gistCreateAdapterListener;

    public GistCreatedDetailFilesAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, GistFile file) {
        if (holder.text != null) {
            holder.text.setText(file.filename);
        }
    }

    public void setGistCreateAdapterListener(GistCreateAdapterListener gistCreateAdapterListener) {
        this.gistCreateAdapterListener = gistCreateAdapterListener;
    }

    public void update(int editingPosition, GistFile file) {
        if (editingPosition > -1) {
            if (getItemCount() > editingPosition) {
                getItems().set(editingPosition, file);
                notifyItemChanged(editingPosition);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gistCreateAdapterListener != null) {
                        gistCreateAdapterListener.updateFile(getAdapterPosition(), getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface GistCreateAdapterListener {
        void updateFile(int position, GistFile gistFile);
    }
}
