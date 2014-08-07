package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumericTitle;
import android.widget.TabTitle;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.star.CheckRepoStarredClient;
import com.alorma.github.sdk.services.star.StarRepoClient;
import com.alorma.github.sdk.services.star.UnstarRepoClient;
import com.alorma.github.ui.adapter.detail.repo.RepoDetailPagerAdapter;
import com.alorma.github.ui.events.ColorEvent;
import com.alorma.github.ui.listeners.RefreshListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailFragment extends Fragment implements RefreshListener, View.OnClickListener, ViewPager.OnPageChangeListener, BaseClient.OnResultCallback<Repo> {
    public static final String OWNER = "OWNER";
    public static final String REPO = "REPO";
    public static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";
    public static final String DESCRIPTION = "DESCRIPTION";

    private String owner;
    private String repo;
    private SmoothProgressBar smoothBar;
    private ViewPager pager;
    private TabTitle tabReadme;
    private TabTitle tabSource;
    private TabTitle tabInfo;
    private List<TabTitle> tabs;
    private boolean fromIntentFilter;
    private TextView textDescription;
    private String description;
    private Bus bus;
    private View repoDetailInfo;
    private boolean repoStarred;
    private boolean repoWatched;

    public static RepoDetailFragment newInstance(String owner, String repo, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        bundle.putString(DESCRIPTION, description);

        RepoDetailFragment f = new RepoDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    public static RepoDetailFragment newInstance(String owner, String repo, String description, boolean fromIntentFilter) {
        Bundle bundle = new Bundle();
        bundle.putString(OWNER, owner);
        bundle.putString(REPO, repo);
        bundle.putString(DESCRIPTION, description);
        bundle.putBoolean(FROM_INTENT_FILTER, fromIntentFilter);

        RepoDetailFragment f = new RepoDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = new Bus();
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.repo_detail_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            owner = getArguments().getString(OWNER);
            repo = getArguments().getString(REPO);
            description = getArguments().getString(DESCRIPTION);
            fromIntentFilter = getArguments().getBoolean(FROM_INTENT_FILTER, false);

            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(owner + "/" + repo);
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(!fromIntentFilter);
            }

            GetRepoClient repoClient = new GetRepoClient(getActivity(), owner, repo);
            repoClient.setOnResultCallback(this);
            repoClient.execute();

            CheckRepoStarredClient starredClient = new CheckRepoStarredClient(getActivity(), owner, repo);
            starredClient.setOnResultCallback(new StarredResult());
            starredClient.execute();

            smoothBar = (SmoothProgressBar) view.findViewById(R.id.smoothBar);

            textDescription = (TextView) view.findViewById(R.id.textDescription);

            if (TextUtils.isEmpty(description)) {
                textDescription.setVisibility(View.GONE);
            } else {
                textDescription.setText(Html.fromHtml(description));
            }

            repoDetailInfo = view.findViewById(R.id.repoDetailInfo);

            repoDetailInfo.setBackgroundColor(GistsApplication.AB_COLOR);

            tabReadme = (TabTitle) view.findViewById(R.id.tabReadme);
            tabSource = (TabTitle) view.findViewById(R.id.tabSource);
            tabInfo = (TabTitle) view.findViewById(R.id.tabInfo);

            tabReadme.setOnClickListener(this);
            tabSource.setOnClickListener(this);
            tabInfo.setOnClickListener(this);

            tabs = new ArrayList<TabTitle>();
            tabs.add(tabReadme);
            tabs.add(tabSource);
            tabs.add(tabInfo);

            pager = (ViewPager) view.findViewById(R.id.pager);
            pager.setOffscreenPageLimit(3);
            pager.setOnPageChangeListener(this);
            pager.setAdapter(new RepoDetailPagerAdapter(getFragmentManager(), owner, repo, this));

            selectButton(tabReadme);
        } else {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.repo_detail_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem starItem = menu.findItem(R.id.action_star);

        if (starItem != null) {
            if (repoStarred) {
                starItem.setTitle(R.string.menu_unstar);
            } else {
                starItem.setTitle(R.string.menu_star);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_star) {
            if (repoStarred) {
                UnstarRepoClient unstarRepoClient = new UnstarRepoClient(getActivity(), owner, repo);
                unstarRepoClient.setOnResultCallback(new UnstarActionResult());
                unstarRepoClient.execute();
            } else {
                StarRepoClient unstarRepoClient = new StarRepoClient(getActivity(), owner, repo);
                unstarRepoClient.setOnResultCallback(new StarActionResult());
                unstarRepoClient.execute();
            }
            showRefresh();
        }

        return false;
    }

    @Override
    public void showRefresh() {
        smoothBar.progressiveStart();
    }

    @Override
    public void cancelRefresh() {
        smoothBar.progressiveStop();
    }


    private void selectButton(TabTitle tabSelected) {
        for (TabTitle tab : tabs) {
            if (tab != null) {
                tab.setSelected(tab == tabSelected);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tabReadme:
                selectButton(tabReadme);
                pager.setCurrentItem(0);
                break;
            case R.id.tabSource:
                pager.setCurrentItem(1);
                selectButton(tabSource);
                break;
            case R.id.tabInfo:
                pager.setCurrentItem(2);
                selectButton(tabInfo);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i) {
            case 0:
                selectButton(tabReadme);
                break;
            case 1:
                selectButton(tabSource);
                break;
            case 2:
                selectButton(tabInfo);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onResponseOk(Repo repo, Response r) {
        if (repo != null) {
            if (getActivity() != null && getActivity().getActionBar() != null) {
                if (repo.parent != null) {
                    getActivity().getActionBar().setSubtitle("fork of " + repo.parent.full_name);
                }
            }
        }
        if (textDescription != null) {
            if (TextUtils.isEmpty(repo.description)) {
                textDescription.setVisibility(View.GONE);
            } else {
                textDescription.setText(Html.fromHtml(repo.description));
            }
        }
        cancelRefresh();
    }

    @Override
    public void onFail(RetrofitError error) {
        if (getActivity() != null) {
            getActivity().finish();
        }
        cancelRefresh();
    }

    @Subscribe
    public void colorReceived(ColorEvent event) {
        repoDetailInfo.setBackgroundColor(event.getRgb());
    }

    private class StarredResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                getActivity().invalidateOptionsMenu();
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse().getStatus() == 404) {
                    repoStarred = false;
                    getActivity().invalidateOptionsMenu();
                }
            }
            cancelRefresh();
        }
    }

    private class UnstarActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = false;
                Toast.makeText(getActivity(), "Repo unstarred", Toast.LENGTH_SHORT).show();
                getActivity().invalidateOptionsMenu();
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            cancelRefresh();
        }
    }

    private class StarActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                Toast.makeText(getActivity(), "Repo starred", Toast.LENGTH_SHORT).show();
                getActivity().invalidateOptionsMenu();
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
            }
            cancelRefresh();
        }
    }
}
