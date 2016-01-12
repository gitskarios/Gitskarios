package com.alorma.github.ui.fragment.base;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 05/08/2014.
 */
public abstract class SecondaryLoadingListFragment<T> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener, View.OnClickListener, AdapterView.OnItemClickListener {

    protected TextView emptyText;
    protected ImageView emptyIcon;
    protected View emptyLy;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton fab;
    private ValueAnimator animator;
    private boolean fabVisible;
    private ListView listView;
    private UpdateReceiver updateReceiver;
    private ArrayAdapter<T> listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.list_fragment_gists, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView(view);

        emptyIcon = (ImageView) view.findViewById(R.id.emptyIcon);
        emptyText = (TextView) view.findViewById(R.id.emptyText);
        emptyLy = view.findViewById(R.id.emptyLayout);

        fab = (FloatingActionButton) view.findViewById(R.id.fabButton);

        loadArguments();
        checkFAB();

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);

        if (swipe != null) {
            swipe.setColorSchemeColors(getResources().getColor(R.color.accent));
        }
        swipe.setOnRefreshListener(this);
        executeRequest();
    }

    protected void executeRequest() {
        startRefresh();
    }

    protected void executePaginatedRequest(int page) {
        startRefresh();
    }

    protected void startRefresh() {
        if (swipe != null) {
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(true);
                }
            });
        }
    }

    protected void stopRefresh() {
        if (swipe != null) {
            swipe.post(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopRefresh();
    }

    protected void setupListView(View view) {
        listView = (ListView) view.findViewById(android.R.id.list);

        if (listView != null) {
            listView.setOnItemClickListener(this);

            listView.setOnScrollListener(this);

            listView.setDivider(getResources().getDrawable(R.drawable.divider_main));
        }
    }

    protected void checkFAB() {
        if (getActivity() != null && fab != null) {
            if (useFAB()) {
                fabVisible = true;
                fab.setOnClickListener(this);
                IconicsDrawable drawable = new IconicsDrawable(getActivity(), getFABGithubIcon()).color(Color.WHITE).actionBar();

                fab.setImageDrawable(drawable);
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }

    protected abstract void loadArguments();

    protected boolean useFAB() {
        return false;
    }

    public void setEmpty() {
        if (getActivity() != null) {
            if (emptyText != null && emptyIcon != null) {
                if (getNoDataIcon() != null && getNoDataText() > 0) {
                    IconicsDrawable iconDrawable = new IconicsDrawable(getActivity(), getNoDataIcon());
                    iconDrawable.color(AttributesUtils.getIconsColor(getActivity()));
                    emptyIcon.setImageDrawable(iconDrawable);

                    emptyText.setText(getNoDataText());

                    emptyLy.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected abstract Octicons.Icon getNoDataIcon();

    protected abstract int getNoDataText();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabButton) {
            fabClick();
        }
    }

    protected void fabClick() {

    }

    protected Octicons.Icon getFABGithubIcon() {
        return Octicons.Icon.oct_squirrel;
    }

    public ArrayAdapter<T> getListAdapter() {
        return this.listAdapter;
    }

    public void setListAdapter(ArrayAdapter<T> adapter) {
        this.listAdapter = adapter;
        if (listView != null) {
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listAdapter != null) {
            T item = listAdapter.getItem(position);
            onListItemClick(item);
        }
    }

    protected abstract void onListItemClick(T item);

    @StyleRes
    public int getTheme() {
        return R.style.AppTheme;
    }

    public void reload() {
        if (getListAdapter() == null || getListAdapter().getCount() == 0) {
            executeRequest();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(updateReceiver);
    }

    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (listView != null && listView.getAdapter() != null && listView.getAdapter().getCount() == 0 && isOnline(context)) {
                reload();
            }
        }

        public boolean isOnline(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
        }
    }
}