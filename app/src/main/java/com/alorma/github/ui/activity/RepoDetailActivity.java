package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.RepoAboutFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import com.alorma.github.ui.fragment.detail.repo.ReadmeFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.ui.view.SlidingTabLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity implements BaseClient.OnResultCallback<Repo>, AdapterView.OnItemSelectedListener {

    public static final String REPO_INFO = "REPO_INFO";

    private Boolean repoStarred = false;
    private Boolean repoWatched = false;

    private Repo currentRepo;
//    private ReadmeFragment readmeFragment;
    private RepoAboutFragment aboutFragment;
    private SourceListFragment sourceListFragment;
    private IssuesListFragment issuesListFragment;
    private CommitsListFragment commitsListFragment;
    private ViewPager viewPager;
    private List<Fragment> listFragments;
    private SlidingTabLayout slidingTabLayout;
    private RepoContributorsFragment repoCollaboratorsFragment;
    private RepoInfo requestRepoInfo;
    private ReadmeFragment readmeFragment;

    public static Intent createLauncherIntent(Context context, RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_detail);

        if (getIntent().getExtras() != null) {
            RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

            setTitle(repoInfo.name);

            slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabStrip);

            slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(this));
            slidingTabLayout.setDividerColors(getResources().getColor(R.color.primary_dark_alpha));
            viewPager = (ViewPager) findViewById(R.id.pager);

            load(repoInfo);

        } else {
            finish();
        }
    }

    private void load(RepoInfo repoInfo) {
        this.requestRepoInfo = repoInfo;
        GetRepoClient repoClient = new GetRepoClient(this, repoInfo);
        repoClient.setOnResultCallback(this);
        repoClient.execute();
    }

    private RepoInfo getRepoInfo() {
        RepoInfo repoInfo = new RepoInfo();
        repoInfo.owner = currentRepo.owner.login;
        repoInfo.name = currentRepo.name;
        if (requestRepoInfo != null && requestRepoInfo.branch != null) {
            repoInfo.branch = requestRepoInfo.branch;
        } else {
            repoInfo.branch = currentRepo.default_branch;
        }

        return repoInfo;
    }

    private class NavigationPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> listFragments;

        public NavigationPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
            super(fm);
            this.listFragments = listFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (listFragments.get(position) != null && listFragments.get(position) instanceof TitleProvider) {
                return getString(((TitleProvider) listFragments.get(position)).getTitle());
            }
            return "";
        }

    }

    protected void getStarWatchData() {
        CheckRepoStarredClient repoStarredClient = new CheckRepoStarredClient(this, currentRepo.owner.login, currentRepo.name);
        repoStarredClient.setOnResultCallback(new StarredResult());
        repoStarredClient.execute();

        CheckRepoWatchedClient repoWatchedClient = new CheckRepoWatchedClient(this, currentRepo.owner.login, currentRepo.name);
        repoWatchedClient.setOnResultCallback(new WatchedResult());
        repoWatchedClient.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.repo_detail_activity, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.share_repo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.setIcon(getResources().getDrawable(R.drawable.abc_ic_menu_share_mtrl_alpha, getTheme()));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.abc_ic_menu_share_mtrl_alpha));
        }

        if (currentRepo == null || currentRepo.parent == null) {
            MenuItem parentItem = menu.findItem(R.id.action_show_parent);
            if (parentItem != null) {
                menu.removeItem(parentItem.getItemId());
            }
        }

        if (repoStarred != null) {
            MenuItem starredItem = menu.findItem(R.id.action_repo_star);
            if (starredItem != null) {
                starredItem.setTitle(repoStarred ? R.string.menu_unstar : R.string.menu_star);
            }
        }

        if (repoWatched != null) {
            MenuItem watchedItem = menu.findItem(R.id.action_repo_watch);
            if (watchedItem != null) {
                watchedItem.setTitle(repoWatched ? R.string.menu_unwatch : R.string.menu_watch);
            }
        }

        return true;
    }

    private Intent getShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, currentRepo.full_name);
        intent.putExtra(Intent.EXTRA_TEXT, currentRepo.svn_url);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_show_parent) {
            if (currentRepo != null && currentRepo.parent != null) {
                startActivity(new UrlsManager(this).manageRepos(Uri.parse(currentRepo.parent.html_url)));
            }
        } else if (item.getItemId() == R.id.share_repo) {
            if (currentRepo != null) {
                Intent intent = getShareIntent();
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.action_repo_watch) {
            changeWatchedStatus();
        } else if (item.getItemId() == R.id.action_repo_star) {
            changeStarStatus();
        }

        return false;
    }

    private void changeStarStatus() {
        if (repoStarred) {
            showProgressDialog(R.style.SpotDialog_UnstarringRepo);
            UnstarRepoClient unstarRepoClient = new UnstarRepoClient(this, currentRepo.owner.login, currentRepo.name);
            unstarRepoClient.setOnResultCallback(new UnstarActionResult());
            unstarRepoClient.execute();
        } else {
            showProgressDialog(R.style.SpotDialog_StarringRepo);
            StarRepoClient starRepoClient = new StarRepoClient(this, currentRepo.owner.login, currentRepo.name);
            starRepoClient.setOnResultCallback(new StarActionResult());
            starRepoClient.execute();
        }
    }

    private void changeWatchedStatus() {
        if (repoWatched) {
            showProgressDialog(R.style.SpotDialog_UnwatchRepo);
            UnwatchRepoClient unwatchRepoClient = new UnwatchRepoClient(this, currentRepo.owner.login, currentRepo.name);
            unwatchRepoClient.setOnResultCallback(new UnwatchActionResult());
            unwatchRepoClient.execute();
        } else {
            showProgressDialog(R.style.SpotDialog_WatchRepo);
            WatchRepoClient watchRepoClient = new WatchRepoClient(this, currentRepo.owner.login, currentRepo.name);
            watchRepoClient.setOnResultCallback(new WatchActionResult());
            watchRepoClient.execute();
        }
    }

    @Override
    public void onResponseOk(Repo repo, Response r) {
        if (repo != null) {
            this.currentRepo = repo;

            setTitle(currentRepo.name);

            getStarWatchData();

            createFragments();

            this.invalidateOptionsMenu();

            if (issuesListFragment != null) {
                issuesListFragment.setPermissions(repo.permissions);
            }
        }
    }

    private void createFragments() {

//        aboutFragment = RepoAboutFragment.newInstance(getRepoInfo());
        readmeFragment = ReadmeFragment.newInstance(getRepoInfo());
        sourceListFragment = SourceListFragment.newInstance(getRepoInfo());
        commitsListFragment = CommitsListFragment.newInstance(getRepoInfo());
        issuesListFragment = IssuesListFragment.newInstance(getRepoInfo(), false);
        repoCollaboratorsFragment = RepoContributorsFragment.newInstance(getRepoInfo(), currentRepo.owner);

        listFragments = new ArrayList<>();
//        listFragments.add(aboutFragment);
        listFragments.add(readmeFragment);
        listFragments.add(sourceListFragment);
        listFragments.add(commitsListFragment);
        listFragments.add(issuesListFragment);
        listFragments.add(repoCollaboratorsFragment);

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));

        viewPager.setOffscreenPageLimit(listFragments.size());

        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onFail(RetrofitError error) {
        ErrorHandler.onError(this, "RepoDetailActivity", error);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Results for STAR
     */
    private class StarredResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            hideProgressDialog();
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            hideProgressDialog();
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoStarred = false;
                    invalidateOptionsMenu();
                }
            }
        }
    }

    private class UnstarActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            hideProgressDialog();
            if (r != null && r.getStatus() == 204) {
                repoStarred = false;
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            hideProgressDialog();
        }
    }

    private class StarActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            hideProgressDialog();
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                invalidateOptionsMenu();
            }

        }

        @Override
        public void onFail(RetrofitError error) {
            hideProgressDialog();
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
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoWatched = false;
                    invalidateOptionsMenu();
                }
            }
        }
    }

    private class UnwatchActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            hideProgressDialog();
            if (r != null && r.getStatus() == 204) {
                repoWatched = false;
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            hideProgressDialog();
        }
    }

    private class WatchActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            hideProgressDialog();
            if (r != null && r.getStatus() == 204) {
                repoWatched = true;
                invalidateOptionsMenu();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            hideProgressDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onBackPressed() {
////        if (viewPager.getCurrentItem() == 1) {
////            sourceListFragment.onBackPressed();
////        } else {
////            super.onBackPressed();
////        }
//        // TODO Source fragment
//    }
}
