package com.alorma.github.ui.fragment.base;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;

import fr.castorflex.android.smoothprogressbar.ContentLoadingSmoothProgressBar;

/**
 * Created by Bernat on 05/08/2014.
 */
public class LoadingListFragment extends ListFragment {

    protected ContentLoadingSmoothProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.list_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ContentLoadingSmoothProgressBar) view.findViewById(R.id.progress);

    }

}