package com.alorma.github.ui.activity.repo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.GitskariosSettings;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.MainActivity;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.github.ui.fragment.detail.repo.RepoContributorsFragment;
import com.alorma.github.ui.fragment.issues.RepositoryIssuesListFragment;
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
  private RepositoryIssuesListFragment repositoryIssuesListFragment;
  private RepoContributorsFragment repoContributorsFragment;

  private TabLayout.Tab codeTab;
  private TabLayout.Tab issuesTab;
  private TabLayout.Tab pullrequestsTab;
  private TabLayout.Tab contributorsTab;

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
        setTitle(requestRepoInfo.name);

        showNewRepoScreenDialog();

        listFragments();
        setupTabs();
        setupContent();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  private void showNewRepoScreenDialog() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    boolean first_time = preferences.getBoolean("repo_screen_first_time", true);

    if (first_time) {
      new MaterialDialog.Builder(this).title("Repository")
          .content("This is the new repository screen!\n\nThere are two new settings to show full readme, and set default tab as selected.")
          .dismissListener(dialog1 -> preferences.edit().putBoolean("repo_screen_first_time", false).apply())
          .show();
    }
  }

  private void listFragments() {
    fragments = new ArrayList<>();

    repoAboutFragment = RepoAboutFragment.newInstance(requestRepoInfo);
    fragments.add(repoAboutFragment);

    repositoryIssuesListFragment = RepositoryIssuesListFragment.newInstance(requestRepoInfo);
    fragments.add(repositoryIssuesListFragment);

    repoContributorsFragment = RepoContributorsFragment.newInstance(requestRepoInfo);
    fragments.add(repoContributorsFragment);
  }

  private void setupTabs() {

    codeTab = getTab(R.drawable.ic_home, R.string.repo_detail_home);
    tabLayout.addTab(codeTab, true);

    issuesTab = getTab(R.drawable.ic_issue_opened, R.string.repo_detail_issues);
    tabLayout.addTab(issuesTab, false);

    contributorsTab = getTab(R.drawable.ic_person, R.string.repo_detail_contributors);
    tabLayout.addTab(contributorsTab, false);

    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
          case 0:
            setFragment(repoAboutFragment);
            break;
          case 1:
            setFragment(repositoryIssuesListFragment);
            break;
          case 2:
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
  }

  private void setupContent() {
    GitskariosSettings gitskariosSettings = new GitskariosSettings(this);

    String repoDefaulTab = gitskariosSettings.getRepoDefaulTab();

    if (getString(R.string.repo_settings_defalut_tab_items_issues_value).equals(repoDefaulTab)) {
      setFragment(repositoryIssuesListFragment);
      issuesTab.select();
    } else if (getString(R.string.repo_settings_defalut_tab_items_contributors_value).equals(repoDefaulTab)) {
      setFragment(repoContributorsFragment);
      contributorsTab.select();
    } else {
      setFragment(repoAboutFragment);
      codeTab.select();
    }
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
