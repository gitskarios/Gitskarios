package com.alorma.gistsapp.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.gistsapp.R;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailFragment extends Fragment {

    public static final String GIST_ID = "GIST_ID";

    public static GistDetailFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(GIST_ID, id);

        GistDetailFragment f = new GistDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gist_detail_layout, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
