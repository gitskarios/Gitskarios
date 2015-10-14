package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
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
import com.alorma.github.ui.fragment.detail.repo.BranchManager;
import com.alorma.github.ui.fragment.detail.repo.PermissionsManager;
import com.alorma.github.ui.fragment.detail.repo.RepoAboutFragment;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;
import com.alorma.github.ui.fragment.issues.PullRequestsListFragment;
import com.alorma.github.ui.fragment.releases.RepoReleasesFragment;
import com.alorma.github.ui.listeners.TitleProvider;
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
import java.util.List;

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
    private List<Fragment> listFragments;
    private RepoInfo requestRepoInfo;
    private ViewGroup viewMiniDrawer;

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
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_info).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_file_directory).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_git_commit).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_issue_opened).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_git_pull_request).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_tag).withIconColorRes(R.color.icons));
        items.add(new PrimaryDrawerItem().withIcon(Octicons.Icon.oct_person).withIconColorRes(R.color.icons));
        Drawer drawer = new DrawerBuilder(this).withInnerShadow(true).withDrawerItems(items).buildView();
        View miniDrawer = new MiniDrawer().withDrawer(drawer).withInnerShadow(true).build(this);

        viewMiniDrawer.addView(miniDrawer);
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
        hideProgressDialog();
        if (repo != null) {
            this.currentRepo = repo;
            invalidateOptionsMenu();

            setTitle(currentRepo.name);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(getRepoInfo().branch);
            }

            RepoAboutFragment aboutFragment = RepoAboutFragment.newInstance(currentRepo, getRepoInfo());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, aboutFragment);
            ft.commit();

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

    /*private void createListFragments() {
        if (listFragments != null) {
            listFragments.clear();
        }
        if (listFragments == null || listFragments.size() == 0 && currentRepo != null) {
            RepoAboutFragment aboutFragment = RepoAboutFragment.newInstance(currentRepo, getRepoInfo());
            SourceListFragment sourceListFragment = SourceListFragment.newInstance(getRepoInfo());
            CommitsListFragment commitsListFragment = CommitsListFragment.newInstance(getRepoInfo());
            IssuesListFragment issuesListFragment = IssuesListFragment.newInstance(getRepoInfo(), false);
            PullRequestsListFragment pullRequestsListFragment = PullRequestsListFragment.newInstance(getRepoInfo());
            RepoReleasesFragment repoReleasesFragment = RepoReleasesFragment.newInstance(getRepoInfo(), currentRepo.permissions);
            RepoContributorsFragment repoCollaboratorsFragment = RepoContributorsFragment.newInstance(getRepoInfo(), currentRepo.owner);

            listFragments = new ArrayList<>();
            listFragments.add(aboutFragment);
            listFragments.add(sourceListFragment);
            listFragments.add(commitsListFragment);
            listFragments.add(issuesListFragment);
            listFragments.add(pullRequestsListFragment);
            listFragments.add(repoReleasesFragment);
            listFragments.add(repoCollaboratorsFragment);
        }
    }*/

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
        } else {
            // TODO -> Source fragment
//            int currentItem = viewPager.getCurrentItem();
//
//            if (listFragments != null && currentItem >= 0 && currentItem < listFragments.size()) {
//                Fragment fragment = listFragments.get(currentItem);
//                if (fragment != null && fragment instanceof BackManager) {
//                    if (((BackManager) fragment).onBackPressed()) {
//                        finish();
//                    }
//                } else {
//                    finish();
//                }
//            } else {
//                finish();
//            }
        }
    }
}
