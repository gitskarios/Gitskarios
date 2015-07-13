package com.alorma.github.ui.adapter.detail.repo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    public RepoSourceAdapter(LayoutInflater inflater) {
        super(inflater);
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
        } else if (ContentType.submodule.equals(content.type)) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_file_symlink_directory);
        } else if (ContentType.file.equals(content.type)) {
            iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_file_text);
        }

        if (iconDrawable != null) {
            iconDrawable.sizeDp(36);
            iconDrawable.paddingDp(8);
            iconDrawable.color(AttributesUtils.getPrimaryLightColor(holder.itemView.getContext()));

            holder.image.setImageDrawable(iconDrawable);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textName;
        private final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.icon);
        }
    }
}
