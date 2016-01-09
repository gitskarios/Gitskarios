package com.alorma.github.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

import java.util.TreeMap;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistsAdapter extends RecyclerArrayAdapter<Gist, GistsAdapter.Holder> {

    private GistsAdapterListener gistsAdapterListener;

    public GistsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(getInflater().inflate(R.layout.row_gist, parent, false));
    }

    @Override
    protected void onBindViewHolder(Holder holder, Gist gist) {
        TreeMap<String, GistFile> filesMap = new TreeMap<>(gist.files);

        GistFile firstFile = filesMap.firstEntry().getValue();

        holder.textFileName.setText(firstFile.filename);

        holder.textNumFiles.setText(holder.itemView.getContext().getString(R.string.num_of_files, gist.files.size()));

        TextView textDescription = (TextView) holder.itemView.findViewById(R.id.textDescription);

        if (!TextUtils.isEmpty(gist.description)) {
            textDescription.setVisibility(View.VISIBLE);
            textDescription.setText(gist.description);
        } else {
            textDescription.setVisibility(View.GONE);
        }

        if (gist.isPublic) {
            holder.gistPrivate.setVisibility(View.GONE);
        } else {
            holder.gistPrivate.setVisibility(View.VISIBLE);
        }
    }

    public void setGistsAdapterListener(GistsAdapterListener gistsAdapterListener) {
        this.gistsAdapterListener = gistsAdapterListener;
    }

    public interface GistsAdapterListener {
        void onGistSelected(Gist gist);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView textFileName;
        private final TextView textNumFiles;
        private final TextView gistPrivate;

        public Holder(View itemView) {
            super(itemView);

            textFileName = (TextView) itemView.findViewById(R.id.textFileName);
            textNumFiles = (TextView) itemView.findViewById(R.id.textNumFiles);
            gistPrivate = (TextView) itemView.findViewById(R.id.gistPrivate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gistsAdapterListener != null) {
                        gistsAdapterListener.onGistSelected(getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
