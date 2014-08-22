package com.alorma.github.ui.fragment.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 05/08/2014.
 */
public abstract class LoadingListFragment extends BaseListFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected TextView emptyText;
    protected ImageView emptyIcon;
    protected View emptyLy;
    private SwipeRefreshLayout swipe;
    private RefreshListener refreshListener;

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

        if (useInnerSwipeRefresh()) {
            swipe.setColorSchemeResources(R.color.accent,
                    R.color.white,
                    R.color.accentDark,
                    R.color.white);
        } else {
            swipe.setColorSchemeResources(R.color.gray_github_light,
                    R.color.gray_github_light,
                    R.color.gray_github_light,
                    R.color.gray_github_light);
        }
        swipe.setOnRefreshListener(this);
    }

    protected boolean useInnerSwipeRefresh() {
        return true;
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    protected void startRefresh() {
        if (useInnerSwipeRefresh() && swipe != null) {
            swipe.setRefreshing(true);
        } else if (refreshListener != null) {
            refreshListener.showRefresh();
        }
    }

    protected void stopRefresh() {
        if (useInnerSwipeRefresh() && swipe != null) {
            swipe.setRefreshing(false);
        } else if (refreshListener != null) {
            refreshListener.cancelRefresh();
        }
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