package com.alorma.github.ui.fragment.base;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Bernat on 12/08/2014.
 */
public abstract class BaseListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get tracker.
        Tracker t = ((GistsApplication) getActivity().getApplication()).getTracker();

        // Set screen name.
        // Where path is a String representing the screen name.
        t.setScreenName(getClass().getSimpleName());

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = getListView();

        if (listView != null) {
            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
        }
        
        loadArguments();
    }

    protected abstract void loadArguments();


}
