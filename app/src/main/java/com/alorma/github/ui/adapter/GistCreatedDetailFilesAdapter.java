package com.alorma.github.ui.adapter;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.text_with_overflow, parent, false));
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

    public interface GistCreateAdapterListener {
        void updateFile(int position, GistFile gistFile);

        void removeFile(int position, GistFile item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView text;
        private final ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gistCreateAdapterListener != null) {
                        gistCreateAdapterListener.updateFile(getAdapterPosition(), getItem(getAdapterPosition()));
                    }
                }
            });

            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.gist_file_created_row_menu);
                    popupMenu.setOnMenuItemClickListener(new MenuListener(getAdapterPosition(), getItem(getAdapterPosition())));
                    popupMenu.show();
                }
            });
        }

        private class MenuListener implements PopupMenu.OnMenuItemClickListener {
            private int adapterPosition;
            private GistFile item;

            public MenuListener(int adapterPosition, GistFile item) {
                this.adapterPosition = adapterPosition;
                this.item = item;
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gist_file_created_removed:
                        if (gistCreateAdapterListener != null) {
                            gistCreateAdapterListener.removeFile(adapterPosition, this.item);
                        }
                        break;
                }
                return false;
            }
        }
    }
}
