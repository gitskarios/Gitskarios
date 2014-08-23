package com.alorma.github.ui.fragment.base;

import android.app.Fragment;
import android.app.ListFragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import fr.dvilleneuve.android.TextDrawable;

/**
 * Created by Bernat on 12/08/2014.
 */
public abstract class BaseListFragment extends ListFragment {

    protected TextView emptyText;
    protected ImageView emptyIcon;
    protected View emptyLy;

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
        return inflater.inflate(R.layout.base_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyIcon = (ImageView) view.findViewById(R.id.emptyIcon);
        emptyText = (TextView) view.findViewById(R.id.emptyText);
        emptyLy = view.findViewById(R.id.emptyLayout);

        ListView listView = getListView();

        if (listView != null) {
            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
        }

        ImageView image = (ImageView) view.findViewById(R.id.fabButton);

        if (image != null) {
            if (useFAB() && fabDrawable() != null) {
                image.setImageDrawable(fabDrawable());
                image.setOnClickListener(fabListener());
            } else {
                image.setVisibility(View.GONE);
            }
        }

        loadArguments();
    }

    protected Drawable fabDrawable() {
        return null;
    }

    protected View.OnClickListener fabListener() {
        return null;
    }

    protected abstract void loadArguments();

    protected boolean useFAB() {
        return false;
    }

    public void setEmpty() {
        if (emptyText != null && emptyIcon != null) {
            if (getNoDataIcon() != null && getNoDataText() > 0) {
                IconDrawable iconDrawable = new IconDrawable(getActivity(), getNoDataIcon());
                iconDrawable.colorRes(R.color.gray_github_medium);
                emptyIcon.setImageDrawable(iconDrawable);

                emptyText.setText(getNoDataText());

                emptyLy.setVisibility(View.VISIBLE);
            }
        }
    }

    protected abstract Iconify.IconValue getNoDataIcon();

    protected abstract int getNoDataText();
}
