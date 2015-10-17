package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Permissions;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.EditRepoClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.callbacks.DialogBranchesCallback;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.fragment.detail.repo.RepoAboutFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;
import com.alorma.github.ui.fragment.issues.PullRequestsListFragment;
import com.alorma.github.ui.fragment.releases.RepoReleasesFragment;
import com.alorma.github.utils.ShortcutUtils;
import com.alorma.gitskarios.core.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity implements BaseClient.OnResultCallback<Repo>, AdapterView.OnItemSelectedListener {

    public static final String FROM_URL = "FROM_URL";
    public static final String REPO_INFO = "REPO_INFO";
    public static final String REPO_INFO_NAME = "REPO_INFO_NAME";
    public static final String REPO_INFO_OWNER = "REPO_INFO_OWNER";

    private static final int EDIT_REPO = 464;

    private Repo currentRepo;
    private RepoInfo requestRepoInfo;
    private ViewGroup viewMiniDrawer;
    private DrawerLayout drawer_layout;
    private Fragment currentFragment;
    private Drawer drawer;
    private MiniDrawer miniDrawer;
    private RepoAboutFragment repoAboutFragment;
    private SourceListFragment sourceListFragment;
    private CommitsListFragment commitsListFragment;
    private IssuesListFragment issuesListFragment;
    private PullRequestsListFragment pullRequestsListFragment;
    private RepoReleasesFragment repoReleasesFragment;
    private RepoContributorsFragment repoContributorsFragment;

    public static Intent createLauncherIntent(Context context, RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent createShortcutLauncherIntent(Context context, RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(REPO_INFO_NAME, repoInfo.name);
        bundle.putString(REPO_INFO_OWNER, repoInfo.owner);

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

            if (repoInfo == null) {
                if (getIntent().getExtras().containsKey(REPO_INFO_NAME) && getIntent().getExtras().containsKey(REPO_INFO_OWNER)) {
                    String name = getIntent().getExtras().getString(REPO_INFO_NAME);
                    String owner = getIntent().getExtras().getString(REPO_INFO_OWNER);

                    repoInfo = new RepoInfo();
                    repoInfo.name = name;
                    repoInfo.owner = owner;
                }
            }

            if (repoInfo != null) {
                setTitle(repoInfo.name);

                drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

                viewMiniDrawer = (ViewGroup) findViewById(R.id.miniDrawerLayout);

                createDrawer();

                load(repoInfo);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    private void createDrawer() {
        ArrayList<IDrawerItem> items = new ArrayList<>();
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_info).withIdentifier(R.id.repo_detail_nav_fragment_info));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_file_directory).withIdentifier(R.id.repo_detail_nav_fragment_source));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_git_commit).withIdentifier(R.id.repo_detail_nav_fragment_commmits));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_issue_opened).withIdentifier(R.id.repo_detail_nav_fragment_issues));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_git_pull_request).withIdentifier(R.id.repo_detail_nav_fragment_pull_request));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_tag).withIdentifier(R.id.repo_detail_nav_fragment_releases));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_person).withIdentifier(R.id.repo_detail_nav_fragment_members));
        drawer = new DrawerBuilder(this).withDrawerItems(items).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                if (drawerItem != null && currentRepo != null) {
                    switch (drawerItem.getIdentifier()) {
                        case R.id.repo_detail_nav_fragment_info:
                            if (repoAboutFragment == null) {
                                repoAboutFragment = RepoAboutFragment.newInstance(currentRepo, getRepoInfo());
                            }
                            setFragment(repoAboutFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_source:
                            if (sourceListFragment == null) {
                                sourceListFragment = SourceListFragment.newInstance(getRepoInfo());
                            }
                            setFragment(sourceListFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_commmits:
                            if (commitsListFragment == null) {
                                commitsListFragment = CommitsListFragment.newInstance(getRepoInfo());
                            }
                            setFragment(commitsListFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_issues:
                            if (issuesListFragment == null) {
                                issuesListFragment = IssuesListFragment.newInstance(getRepoInfo(), false);
                            }
                            setFragment(issuesListFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_pull_request:
                            if (pullRequestsListFragment == null) {
                                pullRequestsListFragment = PullRequestsListFragment.newInstance(getRepoInfo());
                            }
                            setFragment(pullRequestsListFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_releases:
                            if (repoReleasesFragment == null) {
                                repoReleasesFragment = RepoReleasesFragment.newInstance(getRepoInfo(), currentRepo.permissions);
                            }
                            setFragment(repoAboutFragment);
                            break;
                        case R.id.repo_detail_nav_fragment_members:
                            if (repoContributorsFragment == null) {
                                repoContributorsFragment = RepoContributorsFragment.newInstance(getRepoInfo(), currentRepo.owner);
                            }
                            setFragment(repoContributorsFragment);
                            break;
                    }
                    return miniDrawer.onItemClick(drawerItem);
                }
                return false;
            }
        }).buildView();
        miniDrawer = new MiniDrawer().withDrawer(drawer);

        viewMiniDrawer.addView(miniDrawer.build(this));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.repo_detail_activity, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu != null) {
            if (menu.findItem(R.id.action_manage_repo) == null) {
                if (currentRepo != null && currentRepo.permissions != null) {
                    if (currentRepo.permissions.admin) {
                        getMenuInflater().inflate(R.menu.repo_detail_activity_permissions, menu);
                    }
                }
            }

            MenuItem item = menu.findItem(R.id.share_repo);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                item.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha, getTheme()));
            } else {
                item.setIcon(getResources().getDrawable(R.drawable.ic_menu_share_mtrl_alpha));
            }

            MenuItem menuChangeBranch = menu.findItem(R.id.action_repo_change_branch);

            Drawable changeBranch = new IconicsDrawable(this, Octicons.Icon.oct_git_branch).actionBar().colorRes(R.color.white);

            if (menuChangeBranch != null) {
                menuChangeBranch.setIcon(changeBranch);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.share_repo) {
            if (currentRepo != null) {
                String title = currentRepo.full_name;
                String url = currentRepo.svn_url;

                new ShareAction(this, title, url).execute();
            }
        } else if (item.getItemId() == R.id.action_repo_change_branch) {
            changeBranch();
        } else if (item.getItemId() == R.id.action_manage_repo) {
            Intent intent = ManageRepositoryActivity.createIntent(this, getRepoInfo(), createRepoRequest());
            startActivityForResult(intent, EDIT_REPO);
        } else if (item.getItemId() == R.id.action_add_shortcut) {
            ShortcutUtils.addShortcut(this, getRepoInfo());
        }

        return false;
    }

    private RepoRequestDTO createRepoRequest() {
        RepoRequestDTO dto = new RepoRequestDTO();

        dto.isPrivate = currentRepo.isPrivate;
        dto.name = currentRepo.name;
        dto.description = currentRepo.description;
        dto.default_branch = currentRepo.default_branch;
        dto.has_downloads = currentRepo.has_downloads;
        dto.has_wiki = currentRepo.has_wiki;
        dto.has_issues = currentRepo.has_issues;
        dto.homepage = currentRepo.homepage;

        return dto;
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
                                                       if (currentFragment != null && currentFragment instanceof BranchManager) {
                                                           ((BranchManager) currentFragment).setCurrentBranch(branch);
                                                       }
                                                   }

                                                   @Override
                                                   protected void onNoBranches() {

                                                   }
                                               }

        );
        repoBranchesClient.execute();
    }


    @Override
    public void onResponseOk(Repo repo, Response r) {
        hideProgressDialog();
        if (repo != null) {
            this.currentRepo = repo;
            invalidateOptionsMenu();

            setTitle(currentRepo.name);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(getRepoInfo().branch);
            }

            repoAboutFragment = RepoAboutFragment.newInstance(currentRepo, getRepoInfo());
            setFragment(repoAboutFragment);

            this.invalidateOptionsMenu();

            if (currentFragment != null && currentFragment instanceof PermissionsManager) {
                Permissions permissions = repo.permissions;
                ((PermissionsManager) currentFragment).setPermissions(permissions.admin, permissions.push, permissions.pull);
            }
        }
    }

    private void setFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();

        drawer_layout.closeDrawers();
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

        if (requestCode == EDIT_REPO) {
            if (resultCode == RESULT_OK && data != null) {
                RepoRequestDTO repoRequestDTO = data.getParcelableExtra(ManageRepositoryActivity.CONTENT);
                showProgressDialog(R.style.SpotDialog_edit_repo);
                EditRepoClient editRepositoryClient = new EditRepoClient(this, getRepoInfo(), repoRequestDTO);
                editRepositoryClient.setOnResultCallback(this);
                editRepositoryClient.execute();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void close(boolean navigateUp) {
        boolean fromUrl = getIntent().getExtras().getBoolean(FROM_URL, false);
        if (navigateUp && fromUrl) {
            Intent upIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
            finish();
        } else if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
            drawer_layout.closeDrawers();
        } else if (currentFragment != null && currentFragment instanceof BackManager) {
            if (((BackManager) currentFragment).onBackPressed()) {
                finish();
            }
        } else {
            finish();
        }
    }
}
