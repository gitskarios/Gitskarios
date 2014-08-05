package com.alorma.github.ui.adapter.detail.repo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.dto.response.ContentType;
import com.alorma.github.ui.events.ColorEvent;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */
public class RepoContentAdapter extends ArrayAdapter<Content> {

    private final LayoutInflater inflater;
    private final Bus bus;
    private Context context;
    private int rgb;

    public RepoContentAdapter(Context context, List<Content> objects) {
        super(context, 0, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        rgb = GistsApplication.AB_COLOR;
        bus = new Bus();
        bus.register(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.row_content, parent, false);

        TextView textName = (TextView) v.findViewById(R.id.name);
        TextView textSha = (TextView) v.findViewById(R.id.sha);
        ImageView image = (ImageView) v.findViewById(R.id.icon);

        Content item = getItem(position);

        textName.setText(item.name);
        if (item.sha != null) {
            textSha.setText(item.sha);
        }

        IconDrawable iconDrawable = null;
        if (ContentType.dir.equals(item.type)) {
            iconDrawable = new IconDrawable(context, Iconify.IconValue.fa_folder);
        } else if (ContentType.submodule.equals(item.type)) {
            iconDrawable = new IconDrawable(context, Iconify.IconValue.fa_code_fork);
        } else if (ContentType.file.equals(item.type)) {
            iconDrawable = new IconDrawable(context, Iconify.IconValue.fa_file);
        } else if (ContentType.up.equals(item.type)) {
            iconDrawable = new IconDrawable(context, Iconify.IconValue.fa_arrow_circle_o_left);
        }

        if (iconDrawable != null) {
            iconDrawable.sizeDp(28);
            iconDrawable.color(rgb);

            image.setImageDrawable(iconDrawable);
        }

        return v;
    }


    public void registerBus() {
        if (bus != null) {
            bus.register(this);
        }
    }

    public void unregisterBus() {
        if (bus != null) {
            bus.unregister(this);
        }
    }

    @Subscribe
    public void colorReceived(ColorEvent event) {
        this.rgb = event.getRgb();
        notifyDataSetChanged();
    }
}
