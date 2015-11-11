package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.alorma.github.R;
import com.alorma.github.cache.QnCacheProvider;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Branch;
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
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.ShortcutUtils;
import com.alorma.gitskarios.core.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity
    implements BaseClient.OnResultCallback<Repo>, AdapterView.OnItemSelectedListener {

  public static final String FROM_URL = "FROM_URL";
  public static final String REPO_INFO = "REPO_INFO";
  public static final String REPO_INFO_NAME = "REPO_INFO_NAME";
  public static final String REPO_INFO_OWNER = "REPO_INFO_OWNER";

  private static final int EDIT_REPO = 464;

  private Repo currentRepo;
  private RepoInfo requestRepoInfo;
  private ArrayList<Fragment> fragments;
  private ViewPager viewPager;

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
      requestRepoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

      if (requestRepoInfo == null) {
        if (getIntent().getExtras().containsKey(REPO_INFO_NAME) && getIntent().getExtras()
            .containsKey(REPO_INFO_OWNER)) {
          String name = getIntent().getExtras().getString(REPO_INFO_NAME);
          String owner = getIntent().getExtras().getString(REPO_INFO_OWNER);

          requestRepoInfo = new RepoInfo();
          requestRepoInfo.name = name;
          requestRepoInfo.owner = owner;
        }
      }

      if (requestRepoInfo != null) {
        setTitle(requestRepoInfo.name);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.content);

        listFragments();

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        showTabsIcons(tabLayout);

        load();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  private void showTabsIcons(TabLayout tabLayout) {
    for (int i = 0; i < fragments.size(); i++) {
      Fragment fragment = fragments.get(i);
      if (fragment instanceof TitleProvider) {
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        if (tab != null) {
          IIcon iicon = ((TitleProvider) fragment).getTitleIcon();
          if (iicon != null) {
            Drawable icon = getPageTitle(iicon);
            tab.setIcon(icon);
          }
        }
      }
    }
  }

  public Drawable getPageTitle(IIcon icon) {
    return new IconicsDrawable(this, icon).sizeDp(14).colorRes(R.color.white);
  }

  private class NavigationAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public NavigationAdapter(FragmentManager fm, List<Fragment> fragments) {
      super(fm);
      this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments != null ? fragments.size() : 0;
    }
  }

  private void listFragments() {
    if (fragments == null) {
      fragments = new ArrayList<>();
      fragments.add(RepoAboutFragment.newInstance(requestRepoInfo));
      fragments.add(SourceListFragment.newInstance(requestRepoInfo));
      fragments.add(CommitsListFragment.newInstance(requestRepoInfo));
      fragments.add(IssuesListFragment.newInstance(requestRepoInfo, false));
      fragments.add(PullRequestsListFragment.newInstance(requestRepoInfo));
      fragments.add(RepoReleasesFragment.newInstance(requestRepoInfo));
      fragments.add(RepoContributorsFragment.newInstance(requestRepoInfo));
    }
  }

  private void load() {
    GetRepoClient repoClient = new GetRepoClient(this, requestRepoInfo);
    repoClient.observable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Pair<Repo, Response>>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {

          }

          @Override
          public void onNext(Pair<Repo, Response> repoResponsePair) {
            onResponseOk(repoResponsePair.first, repoResponsePair.second);
          }
        });
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

      Drawable changeBranch = new IconicsDrawable(this, Octicons.Icon.oct_git_branch).actionBar()
          .colorRes(R.color.white);

      if (menuChangeBranch != null) {
        menuChangeBranch.setIcon(changeBranch);
      }
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    if (item.getItemId() == android.R.id.home) {
      finish();
    } else if (item.getItemId() == R.id.share_repo) {
      if (currentRepo != null) {
        String title = currentRepo.full_name;
        String url = currentRepo.svn_url;

        new ShareAction(this, title, url).execute();
      }
    } else if (item.getItemId() == R.id.action_repo_change_branch) {
      changeBranch();
    } else if (item.getItemId() == R.id.action_manage_repo) {
      if (currentRepo != null) {
        Intent intent =
            ManageRepositoryActivity.createIntent(this, requestRepoInfo, createRepoRequest());
        startActivityForResult(intent, EDIT_REPO);
      }
    } else if (item.getItemId() == R.id.action_add_shortcut) {
      ShortcutUtils.addShortcut(this, requestRepoInfo);
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
    GetRepoBranchesClient repoBranchesClient = new GetRepoBranchesClient(this, requestRepoInfo);
    BaseClient.OnResultCallback<List<Branch>> callback =
        new DialogBranchesCallback(this, requestRepoInfo) {
          @Override
          protected void onBranchSelected(String branch) {
            requestRepoInfo.branch = branch;
            if (currentRepo != null) {
              currentRepo.default_branch = branch;
            }
            if (getSupportActionBar() != null) {
              getSupportActionBar().setSubtitle(branch);
            }
            for (Fragment fragment : fragments) {
              if (fragment instanceof BranchManager) {
                ((BranchManager) fragment).setCurrentBranch(branch);
              }
            }
          }

          @Override
          protected void onNoBranches() {

          }
        };
    repoBranchesClient.setOnResultCallback(callback);
    repoBranchesClient.execute();
  }

  @Override
  public void onResponseOk(Repo repo, Response r) {
    hideProgressDialog();
    if (repo != null) {
      this.currentRepo = repo;

      requestRepoInfo.branch = repo.default_branch;
      requestRepoInfo.permissions = repo.permissions;

      invalidateOptionsMenu();

      setTitle(currentRepo.name);
      if (getSupportActionBar() != null) {
        getSupportActionBar().setSubtitle(requestRepoInfo.branch);
      }

      this.invalidateOptionsMenu();

      Permissions permissions = repo.permissions;
      for (Fragment fragment : fragments) {
        if (fragment instanceof PermissionsManager) {
          ((PermissionsManager) fragment).setPermissions(permissions.admin, permissions.push,
              permissions.pull);
        }
        if (fragment instanceof BranchManager) {
          ((BranchManager) fragment).setCurrentBranch(currentRepo.default_branch);
        }
      }

      QnCacheProvider.getInstance(QnCacheProvider.TYPE.REPO).set(requestRepoInfo.toString(), repo);
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

    if (requestCode == EDIT_REPO) {
      if (resultCode == RESULT_OK && data != null) {
        RepoRequestDTO repoRequestDTO = data.getParcelableExtra(ManageRepositoryActivity.CONTENT);
        showProgressDialog(R.style.SpotDialog_edit_repo);
        EditRepoClient editRepositoryClient =
            new EditRepoClient(this, requestRepoInfo, repoRequestDTO);
        editRepositoryClient.setOnResultCallback(this);
        editRepositoryClient.execute();
      } else if (resultCode == RESULT_CANCELED) {
        finish();
      }
    }
  }

  @Override
  protected void close(boolean navigateUp) {
    if (fragments != null) {
      boolean fromUrl = getIntent().getExtras().getBoolean(FROM_URL, false);
      Fragment currentFragment = fragments.get(viewPager.getCurrentItem());
      if (navigateUp && fromUrl) {
        Intent upIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        finish();
      } else if (currentFragment != null && currentFragment instanceof BackManager) {
        if (((BackManager) currentFragment).onBackPressed()) {
          finish();
        }
      } else {
        finish();
      }
    } else {
      finish();
    }
  }
}
