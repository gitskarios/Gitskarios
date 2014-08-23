package com.alorma.github.ui.fragment.detail.repo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabTitle;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.adapter.detail.repo.RepoDetailPagerAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.RefreshListener;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailFragment extends BaseFragment implements RefreshListener, View.OnClickListener,
        ViewPager.OnPageChangeListener, BaseClient.OnResultCallback<Repo> {
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
    private TabTitle tabIssues;
    private List<TabTitle> tabs;
    private boolean fromIntentFilter;
    private boolean repoStarred;
    private boolean repoWatched;
    private Repo currentRepo;
    private boolean showParentMenu;
    private RepoDetailPagerAdapter pagerAdapter;
    private Integer refreshItems = null;
    private View view;

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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.repo_detail_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            load();
        } else {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private void load() {
        owner = getArguments().getString(OWNER);
        repo = getArguments().getString(REPO);
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

        CheckRepoWatchedClient watcheClien = new CheckRepoWatchedClient(getActivity(), owner, repo);
        watcheClien.setOnResultCallback(new WatchedResult());
        watcheClien.execute();

        smoothBar = (SmoothProgressBar) view.findViewById(R.id.smoothBar);

        tabReadme = (TabTitle) view.findViewById(R.id.tabReadme);
        tabSource = (TabTitle) view.findViewById(R.id.tabSource);
        tabIssues = (TabTitle) view.findViewById(R.id.tabIssues);

        tabReadme.setOnClickListener(this);
        tabSource.setOnClickListener(this);
        tabIssues.setOnClickListener(this);

        tabs = new ArrayList<TabTitle>();
        tabs.add(tabReadme);
        tabs.add(tabSource);
        tabs.add(tabIssues);

        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);
        pager.setOnPageChangeListener(this);
        pagerAdapter = new RepoDetailPagerAdapter(getFragmentManager(), owner, repo, this);
        pager.setAdapter(pagerAdapter);

        selectButton(tabReadme);
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

        MenuItem watchItem = menu.findItem(R.id.action_watch);

        if (watchItem != null) {
            if (repoWatched) {
                watchItem.setTitle(R.string.menu_unwatch);
            } else {
                watchItem.setTitle(R.string.menu_watch);
            }
        }

        if (currentRepo != null && currentRepo.parent == null && !showParentMenu) {
            showParentMenu = true;
            menu.removeItem(R.id.action_show_parent);
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
                StarRepoClient starRepoClient = new StarRepoClient(getActivity(), owner, repo);
                starRepoClient.setOnResultCallback(new StarActionResult());
                starRepoClient.execute();
            }
            showRefresh();
        } else if (item.getItemId() == R.id.action_watch) {
            if (repoWatched) {
                UnwatchRepoClient unwatchRepoClient = new UnwatchRepoClient(getActivity(), owner, repo);
                unwatchRepoClient.setOnResultCallback(new UnwatchActionResult());
                unwatchRepoClient.execute();
            } else {
                WatchRepoClient watchRepoClient = new WatchRepoClient(getActivity(), owner, repo);
                watchRepoClient.setOnResultCallback(new WatchActionResult());
                watchRepoClient.execute();
            }
            showRefresh();
        } else if (item.getItemId() == R.id.action_show_parent) {
            if (currentRepo != null && currentRepo.parent != null) {
                String parentFullName = currentRepo.parent.full_name;
                String[] split = parentFullName.split("/");
                String owner = split[0];
                String name = split[1];

                Intent launcherActivity = RepoDetailActivity.createLauncherActivity(getActivity(), owner, name, currentRepo.parent.description);
                startActivity(launcherActivity);
            }
        }

        return false;
    }

    @Override
    public void showRefresh() {
        if (refreshItems == null) {
            smoothBar.progressiveStart();
            refreshItems = 1;
        } else {
            refreshItems++;
        }
    }

    @Override
    public void cancelRefresh() {
        if (refreshItems != null) {
            refreshItems--;

            if (refreshItems == 0) {
                refreshItems = null;
            }
        }
        if (refreshItems == null) {
            smoothBar.progressiveStop();
        }
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
            case R.id.tabIssues:
                pager.setCurrentItem(2);
                selectButton(tabIssues);
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
                selectButton(tabIssues);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onResponseOk(Repo repo, Response r) {
        if (getActivity() != null) {
            if (repo != null) {
                this.currentRepo = repo;
                getActivity().invalidateOptionsMenu();

                if (getActivity().getActionBar() != null) {
                    if (repo.parent != null) {
                        getActivity().getActionBar().setSubtitle(getResources().getString(R.string.fork_of, repo.parent.full_name));
                    }
                }
            }
            cancelRefresh();
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onRetrofitError(getActivity(), "RepoDetailFragment", error);
        if (getActivity() != null) {
            getActivity().finish();
        }
        cancelRefresh();
    }

    /**
     * Results for STAR
     */
    private class StarredResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoStarred = false;
                    if (getActivity() != null) {
                        getActivity().invalidateOptionsMenu();
                    }
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
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
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
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
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

    /**
     * RESULTS FOR WATCH
     */

    private class WatchedResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = true;
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoWatched = false;
                    if (getActivity() != null) {
                        getActivity().invalidateOptionsMenu();
                    }
                }
            }
            cancelRefresh();
        }
    }

    private class UnwatchActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = false;
                Toast.makeText(getActivity(), "Not watching repo", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            cancelRefresh();
        }
    }

    private class WatchActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = true;
                Toast.makeText(getActivity(), "Watching repo", Toast.LENGTH_SHORT).show();

                if (getActivity() != null) {
                    getActivity().invalidateOptionsMenu();
                }
            }
            cancelRefresh();
        }

        @Override
        public void onFail(RetrofitError error) {
            cancelRefresh();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
