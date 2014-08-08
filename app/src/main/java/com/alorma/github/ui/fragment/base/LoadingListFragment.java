package com.alorma.github.ui.fragment.base;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Bernat on 05/08/2014.
 */
public abstract class LoadingListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected TextView emptyText;
    protected ImageView emptyIcon;
    protected View emptyLy;
    protected SwipeRefreshLayout swipe;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.list_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        emptyIcon = (ImageView) view.findViewById(R.id.emptyIcon);
        emptyText = (TextView) view.findViewById(R.id.emptyText);
        emptyLy = view.findViewById(R.id.emptyLayout);

        swipe.setColorSchemeResources(R.color.accentDark,
                R.color.complementary,
                R.color.accentDark,
                R.color.gray_github_dark);
        swipe.setOnRefreshListener(this);

    }
}