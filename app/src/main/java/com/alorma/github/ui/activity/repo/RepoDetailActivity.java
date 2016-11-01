package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.injector.component.ApplicationComponent;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.issues.RepositoryIssuesListFragment;
import com.alorma.github.ui.fragment.issues.RepositoryPullRequestsListFragment;
import java.util.ArrayList;

public class RepoDetailActivity extends RepositoryThemeActivity {

  @BindView(R.id.tabLayout) TabLayout tabLayout;

  public static final String FROM_URL = "FROM_URL";
  public static final String REPO_INFO = "REPO_INFO";
  public static final String REPO_INFO_NAME = "REPO_INFO_NAME";
  public static final String REPO_INFO_OWNER = "REPO_INFO_OWNER";

  //private Repo currentRepo;
  private RepoInfo requestRepoInfo;
  private ArrayList<Fragment> fragments;
  private RepoAboutFragment repoAboutFragment;
  private RepoReadmeFragment repoReadmeFragment;
  private RepositoryIssuesListFragment repositoryIssuesListFragment;
  private RepoContributorsFragment repoContributorsFragment;
  private RepositoryPullRequestsListFragment repositoryPullRequestsListFragment;

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

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      requestRepoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

      if (requestRepoInfo == null) {
        if (getIntent().getExtras().containsKey(REPO_INFO_NAME) && getIntent().getExtras().containsKey(REPO_INFO_OWNER)) {
          String name = getIntent().getExtras().getString(REPO_INFO_NAME);
          String owner = getIntent().getExtras().getString(REPO_INFO_OWNER);

          requestRepoInfo = new RepoInfo();
          requestRepoInfo.name = name;
          requestRepoInfo.owner = owner;
        }
      }

      if (requestRepoInfo != null) {
        if (TextUtils.isEmpty(requestRepoInfo.branch)) {
          requestRepoInfo.branch = "master";
        }
        setTitle(requestRepoInfo.name);

        listFragments();
        setupTabs();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  private void listFragments() {
    fragments = new ArrayList<>();

    repoAboutFragment = RepoAboutFragment.newInstance(requestRepoInfo);
    fragments.add(repoAboutFragment);

    repoReadmeFragment = RepoReadmeFragment.newInstance(requestRepoInfo);
    fragments.add(repoReadmeFragment);

    repositoryPullRequestsListFragment = RepositoryPullRequestsListFragment.newInstance(requestRepoInfo);
    fragments.add(repositoryPullRequestsListFragment);

    repositoryIssuesListFragment = RepositoryIssuesListFragment.newInstance(requestRepoInfo, false);
    fragments.add(repositoryIssuesListFragment);

    repoContributorsFragment = RepoContributorsFragment.newInstance(requestRepoInfo);
    fragments.add(repoContributorsFragment);
  }

  private void setupTabs() {

    tabLayout.addTab(getTab(R.drawable.ic_home, R.string.repo_detail_home), true);
    tabLayout.addTab(getTab(R.drawable.ic_file, R.string.repo_detail_readme), false);
    tabLayout.addTab(getTab(R.drawable.ic_issue_opened, R.string.repo_detail_issues), false);
    tabLayout.addTab(getTab(R.drawable.ic_git_pull_request, R.string.repo_detail_pulls), false);
    tabLayout.addTab(getTab(R.drawable.ic_person, R.string.repo_detail_contributors), false);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            setFragment(repoAboutFragment);
            break;
          case 1:
            setFragment(repoReadmeFragment);
            break;
          case 2:
            setFragment(repositoryIssuesListFragment);
            break;
          case 3:
            setFragment(repositoryPullRequestsListFragment);
            break;
          case 4:
            setFragment(repoContributorsFragment);
            break;
        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });

    setFragment(repoAboutFragment);
  }

  @NonNull
  private TabLayout.Tab getTab(@DrawableRes int drawable, @StringRes int text) {
    boolean tabletMode = getResources().getBoolean(R.bool.md_is_tablet);
    TabLayout.Tab tab = tabLayout.newTab();
    if (tabletMode) {
      tab.setText(text);
    } else {
      tab.setIcon(drawable).setText("");
    }
    return tab;
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction fm = getSupportFragmentManager().beginTransaction();
    fm.replace(R.id.content, fragment);
    fm.commit();
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  protected void close(boolean navigateUp) {
    if (fragments != null) {
      boolean fromUrl = getIntent().getExtras().getBoolean(FROM_URL, false);
      Fragment currentFragment = fragments.get(tabLayout.getSelectedTabPosition());
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
