package com.alorma.github.ui.adapter.detail.repo;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ContentType;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 20/07/2014.
 */
public class RepoSourceAdapter extends RecyclerArrayAdapter<Content, RepoSourceAdapter.ViewHolder> {

    private SourceAdapterListener sourceAdapterListener;
    private Context context;

    public RepoSourceAdapter(Context context, LayoutInflater inflater) {
        super(inflater);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_content, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Content content) {
        holder.textName.setText(content.name);

        IconicsDrawable iconDrawable = null;
        if (ContentType.dir.equals(content.type)) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_file_directory);
        } else if (ContentType.symlink.equals(content.type)) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_file_symlink_directory);
        } else if (ContentType.file.equals(content.type)) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_file_text);
        }

        if (iconDrawable != null) {
            iconDrawable.color(AttributesUtils.getAccentColor(context));

            holder.image.setImageDrawable(iconDrawable);
        }
    }

    public void setSourceAdapterListener(SourceAdapterListener sourceAdapterListener) {
        this.sourceAdapterListener = sourceAdapterListener;
    }

    public interface SourceAdapterListener {
        void onContentClick(Content content);

        void onContentMenuAction(Content content, MenuItem menuItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        private final TextView textName;
        private final ImageView image;
        private final ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.icon);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sourceAdapterListener != null) {
                        sourceAdapterListener.onContentClick(getItem(getAdapterPosition()));
                    }
                }
            });

            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    popupMenu.inflate(R.menu.repo_content_item);
                    popupMenu.setOnMenuItemClickListener(ViewHolder.this);
                    popupMenu.show();
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (sourceAdapterListener != null) {
                sourceAdapterListener.onContentMenuAction(getItem(getAdapterPosition()), item);
            }
            return false;
        }
    }
}
