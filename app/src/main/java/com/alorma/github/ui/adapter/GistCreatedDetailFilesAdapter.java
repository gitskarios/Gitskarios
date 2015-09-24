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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView text;
        public final ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }
}
