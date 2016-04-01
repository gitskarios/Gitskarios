package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.pullrequest.PullRequestCommitsListFragment;
import com.alorma.github.ui.fragment.pullrequest.PullRequestDetailOverviewFragment;
import com.alorma.github.ui.fragment.pullrequest.PullRequestFilesListFragment;
import com.alorma.github.utils.AttributesUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;
import java.util.ArrayList;
import java.util.List;

public class PullRequestDetailActivity extends BackActivity
    implements PullRequestDetailOverviewFragment.PullRequestStoryLoaderInterface {

  public static final String ISSUE_INFO = "ISSUE_INFO";
  public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
  public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
  public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

  private IssueInfo issueInfo;
  private BottomBar mBottomBar;
  private BottomBarBadge badgeFiles;
  private BottomBarBadge badgeCommits;

  public static Intent createLauncherIntent(Context context, IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putParcelable(ISSUE_INFO, issueInfo);

    Intent intent = new Intent(context, PullRequestDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  public static Intent createShortcutLauncherIntent(Context context, IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putString(ISSUE_INFO_REPO_NAME, issueInfo.repoInfo.name);
    bundle.putString(ISSUE_INFO_REPO_OWNER, issueInfo.repoInfo.owner);
    bundle.putInt(ISSUE_INFO_NUMBER, issueInfo.num);

    Intent intent = new Intent(context, PullRequestDetailActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.generic_toolbar_coordinator);

    if (getIntent().getExtras() != null) {

      issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);

      if (issueInfo == null && getIntent().getExtras().containsKey(ISSUE_INFO_NUMBER)) {
        String name = getIntent().getExtras().getString(ISSUE_INFO_REPO_NAME);
        String owner = getIntent().getExtras().getString(ISSUE_INFO_REPO_OWNER);

        RepoInfo repoInfo = new RepoInfo();
        repoInfo.name = name;
        repoInfo.owner = owner;

        int num = getIntent().getExtras().getInt(ISSUE_INFO_NUMBER);

        issueInfo = new IssueInfo();
        issueInfo.repoInfo = repoInfo;
        issueInfo.num = num;
      }

      createBottom(savedInstanceState);
    }
  }

  private void createBottom(Bundle savedInstanceState) {
    mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.coordinator),
        findViewById(R.id.content), savedInstanceState);
    mBottomBar.useOnlyStatusBarTopOffset();

    final List<Fragment> fragments = new ArrayList<>();

    PullRequestDetailOverviewFragment overviewFragment =
        PullRequestDetailOverviewFragment.newInstance(issueInfo);
    overviewFragment.setPullRequestStoryLoaderInterface(this);

    fragments.add(overviewFragment);
    fragments.add(PullRequestFilesListFragment.newInstance(issueInfo));
    fragments.add(PullRequestCommitsListFragment.newInstance(issueInfo));

    mBottomBar.setItems(
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_comment_discussion), "Conversation"),
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_file_code), "Files"),
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_git_commit), "Commits"));

    mBottomBar.selectTabAtPosition(0, false);
    mBottomBar.setActiveTabColor(AttributesUtils.getPrimaryColor(this));

    mBottomBar.setOnTabClickListener(new OnTabClickListener() {
      @Override
      public void onTabSelected(int position) {
        selectItem(position);
      }

      private void selectItem(int position) {
        selectFragment(fragments.get(position));
      }

      @Override
      public void onTabReSelected(int position) {
        selectItem(position);
      }
    });

    SharedPreferences defaultSharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String pref_theme =
        defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    mBottomBar.useDarkTheme("theme_dark".equalsIgnoreCase(pref_theme));
  }

  private void selectFragment(Fragment fragment) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment fragmentByTag =
        getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
    if (fragmentByTag != null) {
      ft.replace(R.id.content, fragmentByTag, fragmentByTag.getClass().getSimpleName());
    } else {
      ft.replace(R.id.content, fragment, fragment.getClass().getSimpleName());
    }
    ft.commit();
  }

  private IconicsDrawable getBottomTabIcon(IIcon icon) {
    return new IconicsDrawable(this).icon(icon).sizeDp(20);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mBottomBar.onSaveInstanceState(outState);
  }

  @Override
  public void onStoryLoaded(PullRequestStory story) {
    if (mBottomBar != null && story != null) {
      if (badgeFiles == null) {
        badgeFiles = mBottomBar.makeBadgeForTabAt(1, AttributesUtils.getAccentColor(this),
            story.pullRequest.changed_files);
      }
      badgeFiles.setText(String.valueOf(story.pullRequest.changed_files));
      badgeFiles.setAutoShowAfterUnSelection(true);

      if (badgeCommits == null) {
        badgeCommits = mBottomBar.makeBadgeForTabAt(2, AttributesUtils.getAccentColor(this),
            story.pullRequest.commits);
      }
      badgeCommits.setText(String.valueOf(story.pullRequest.commits));
      badgeCommits.setAutoShowAfterUnSelection(true);
    }
  }
}
