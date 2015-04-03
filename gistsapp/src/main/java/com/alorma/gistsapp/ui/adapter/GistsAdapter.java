package com.alorma.gistsapp.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alorma.gistsapp.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.GistFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistsAdapter extends LazyAdapter<Gist> {
    private final LayoutInflater mInflater;

    public GistsAdapter(Context context, List<Gist> objects) {
        super(context, 0, objects);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.row_gist, parent, false);

        Gist gist = getItem(position);

        TextView textFileName = (TextView) view.findViewById(R.id.textFileName);
        TextView textNumFiles = (TextView) view.findViewById(R.id.textNumFiles);
        TextView gistPrivate = (TextView) view.findViewById(R.id.gistPrivate);

        TreeMap<String, GistFile> filesMap = new TreeMap<>(gist.files);
        GistFile firstFile = filesMap.firstEntry().getValue();
        textFileName.setText(firstFile.filename);

        textNumFiles.setText(view.getContext().getString(R.string.num_of_files, gist.files.size()));

        TextView textDescription = (TextView) view.findViewById(R.id.textDescription);

        if (!TextUtils.isEmpty(gist.description)) {
            textDescription.setVisibility(View.VISIBLE);
            textDescription.setText(gist.description);
        } else {
            textDescription.setVisibility(View.GONE);
        }

        if (gist.isPublic) {
            gistPrivate.setVisibility(View.GONE);
        } else {
            gistPrivate.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
