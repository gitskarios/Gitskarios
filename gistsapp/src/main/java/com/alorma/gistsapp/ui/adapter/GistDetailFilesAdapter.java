package com.alorma.gistsapp.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.gistsapp.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailFilesAdapter extends RecyclerView.Adapter<GistDetailFilesAdapter.ViewHolder> {

    private Map<String, GistFile> files;
    private List<GistFile> gistFileList;

    public GistDetailFilesAdapter() {
        files = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gist_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GistFile gistFile = gistFileList.get(position);

        holder.textFileName.setText(gistFile.filename);
        String content = gistFile.content;

        if (content.length() > 276) {
            content = content.substring(0, 276);
        }

        holder.textContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void add(String key, GistFile item) {
        files.put(key, item);
        gistFileList = new ArrayList<>(files.values());
        notifyItemInserted(files.size());
    }

    public void addAll(Map<String, GistFile> items) {
        files.putAll(items);
        gistFileList = new ArrayList<>(files.values());
        notifyItemInserted(files.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textFileName;
        public final TextView textContent;

        public ViewHolder(View itemView) {
            super(itemView);
            textFileName = (TextView) itemView.findViewById(R.id.textFileName);
            textContent = (TextView) itemView.findViewById(R.id.textContent);
        }
    }
}
