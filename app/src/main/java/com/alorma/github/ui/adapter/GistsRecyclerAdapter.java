package com.alorma.github.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;

import java.util.List;

/**
 * Created by Bernat on 12/07/2014.
 */
public class GistsRecyclerAdapter extends RecyclerView.Adapter<GistsHolder> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<Gist> gists;

    public GistsRecyclerAdapter(Context context, List<Gist> gists) {
        this.context = context;
        this.gists = gists;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public GistsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mInflater.inflate(R.layout.row_gist, viewGroup, true);
        return new GistsHolder(v);
    }

    @Override
    public void onBindViewHolder(GistsHolder gistsHolder, int i) {
        String description = gists.get(i).getDescription();
        if (TextUtils.isEmpty(description)) {
            description = context.getResources().getString(R.string.no_gist_description);
        }
        gistsHolder.text1.setText(description);
    }

    @Override
    public int getItemCount() {
        return gists.size();
    }

    public void add(Gist gist){
        gists.add(gist);
        notifyDataSetChanged();
    }

    public void addAll(List<Gist> gists) {
        gists.addAll(gists);
        notifyDataSetChanged();
    }

    public void clear() {
        gists.clear();
        notifyDataSetChanged();
    }
}
