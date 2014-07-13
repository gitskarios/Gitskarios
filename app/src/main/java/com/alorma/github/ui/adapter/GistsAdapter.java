package com.alorma.github.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;

import java.util.List;

/**
 * Created by Bernat on 12/07/2014.
 */
public class GistsAdapter extends ArrayAdapter<Gist> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<Gist> gists;

    public GistsAdapter(Context context, List<Gist> gists) {
        super(context, 0, 0, gists);
        this.context = context;
        this.gists = gists;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
        GistsHolder gistsHolder = new GistsHolder(v);

        Gist gist = gists.get(position);

        String description = gist.getDescription();
        if (TextUtils.isEmpty(description)) {
            description = context.getResources().getString(R.string.no_gist_description);
        }
        gistsHolder.text1.setText(position + " //// " + description);
        gistsHolder.text2.setText("Num files: " + gist.getFiles().size());

        return v;
    }


    public interface BottomReachedListener {
        void onBotomReached();
    }
}
