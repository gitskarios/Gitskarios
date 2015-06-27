package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.fragment.detail.repo.ReadmeFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoAboutFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.basesdk.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

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
    private ViewPager viewPager;
    private List<Fragment> listFragments;
    private TabLayout slidingTabLayout;
    private RepoInfo requestRepoInfo;

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

            slidingTabLayout = (TabLayout) findViewById(R.id.tabStrip);

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
        if (currentRepo != null) {
            if (currentRepo.owner != null) {
                repoInfo.owner = currentRepo.owner.login;
            }
            repoInfo.name = currentRepo.name;
            if (requestRepoInfo != null && requestRepoInfo.branch != null) {
                repoInfo.branch = requestRepoInfo.branch;
            } else {
                repoInfo.branch = currentRepo.default_branch;
            }
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

        MenuItem menuChangeBranch = menu.findItem(R.id.action_repo_change_branch);

        Drawable changeBranch = new IconicsDrawable(this, Octicons.Icon.oct_git_branch).actionBar().colorRes(R.color.white);

        if (menuChangeBranch != null) {
            menuChangeBranch.setIcon(changeBranch);
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

        if (item.getItemId() == R.id.share_repo) {
            if (currentRepo != null) {
                Intent intent = getShareIntent();
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.action_repo_change_branch) {
            changeBranch();
        }

        return false;
    }

    private void changeBranch() {
        GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(this, getRepoInfo());
        repoBranchesClient.setOnResultCallback(new DialogBranchesCallback(this, getRepoInfo()) {
            @Override
            protected void onBranchSelected(String branch) {
                currentRepo.default_branch = branch;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle(branch);
                }
                if (listFragments != null) {
                    for (Fragment fragment : listFragments) {
                        if (fragment instanceof BranchManager) {
                            ((BranchManager) fragment).setCurrentBranch(branch);
                        }
                    }
                }
            }

            @Override
            protected void onNoBranches() {

            }
        });
        repoBranchesClient.execute();
    }


    @Override
    public void onResponseOk(Repo repo, Response r) {
        if (repo != null) {
            this.currentRepo = repo;

            setTitle(currentRepo.name);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(getRepoInfo().branch);
            }

            createFragments();

            this.invalidateOptionsMenu();

            if (listFragments != null) {
                for (Fragment fragment : listFragments) {
                    if (fragment instanceof PermissionsManager) {
                        Permissions permissions = repo.permissions;
                        ((PermissionsManager) fragment).setPermissions(permissions.admin, permissions.push, permissions.pull);
                    }
                }
            }
        }
    }

    private void createFragments() {

        createListFragments();

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));

        viewPager.setOffscreenPageLimit(listFragments.size());

        slidingTabLayout.setupWithViewPager(viewPager);
    }

    private void createListFragments() {
        if (listFragments == null || listFragments.size() == 0 && currentRepo != null) {
            RepoAboutFragment aboutFragment = RepoAboutFragment.newInstance(currentRepo, getRepoInfo());
            ReadmeFragment readmeFragment = ReadmeFragment.newInstance(getRepoInfo());
            SourceListFragment sourceListFragment = SourceListFragment.newInstance(getRepoInfo());
            CommitsListFragment commitsListFragment = CommitsListFragment.newInstance(getRepoInfo());
            IssuesListFragment issuesListFragment = IssuesListFragment.newInstance(getRepoInfo(), false);
            RepoContributorsFragment repoCollaboratorsFragment = RepoContributorsFragment.newInstance(getRepoInfo(), currentRepo.owner);

            listFragments = new ArrayList<>();
            listFragments.add(aboutFragment);
//            listFragments.add(readmeFragment);
            listFragments.add(sourceListFragment);
            listFragments.add(commitsListFragment);
            listFragments.add(issuesListFragment);
            listFragments.add(repoCollaboratorsFragment);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();

        if (listFragments != null && currentItem >= 0 && currentItem < listFragments.size()) {
            Fragment fragment = listFragments.get(currentItem);
            if (fragment != null && fragment instanceof BackManager) {
                if (((BackManager) fragment).onBackPressed()) {
                    super.onBackPressed();
                }
            }
        } else {
            super.onBackPressed();
        }
    }
}
