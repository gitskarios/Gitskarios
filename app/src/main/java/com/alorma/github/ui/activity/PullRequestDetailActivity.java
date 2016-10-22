package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.ui.actions.ShareAction;
import com.alorma.github.ui.actions.ViewInAction;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.alorma.github.ui.fragment.pullrequest.PullRequestCommitsListFragment;
import com.alorma.github.ui.fragment.pullrequest.PullRequestConversationFragment;
import com.alorma.github.ui.fragment.pullrequest.PullRequestFilesListFragment;
import com.alorma.github.ui.fragment.pullrequest.PullRequestInfoFragment;
import com.alorma.github.utils.ShortcutUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;

public class PullRequestDetailActivity extends RepositoryThemeActivity
    implements PullRequestConversationFragment.PullRequestStoryLoaderInterface {

  public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
  public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
  public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

  @BindView(R.id.bottomBar) BottomBar mBottomBar;
  private IssueInfo issueInfo;
  private PullRequestInfoFragment pullRequestInfoFragment;
  private PullRequestStory story;
  private PullRequestConversationFragment pullRequestConversationFragment;
  private PullRequestFilesListFragment pullRequestFilesListFragment;
  private PullRequestCommitsListFragment pullRequestCommitsListFragment;

  public static Intent createLauncherIntent(Context context, IssueInfo issueInfo) {
    Bundle bundle = new Bundle();

    bundle.putString(ISSUE_INFO_REPO_NAME, issueInfo.repoInfo.name);
    bundle.putString(ISSUE_INFO_REPO_OWNER, issueInfo.repoInfo.owner);
    bundle.putInt(ISSUE_INFO_NUMBER, issueInfo.num);

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
    setContentView(R.layout.pullrequest_activity);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {

      String name = getIntent().getExtras().getString(ISSUE_INFO_REPO_NAME);
      String owner = getIntent().getExtras().getString(ISSUE_INFO_REPO_OWNER);

      RepoInfo repoInfo = new RepoInfo();
      repoInfo.name = name;
      repoInfo.owner = owner;

      int num = getIntent().getExtras().getInt(ISSUE_INFO_NUMBER);

      issueInfo = new IssueInfo();
      issueInfo.repoInfo = repoInfo;
      issueInfo.num = num;

      createBottom();
    }
  }

  private void createBottom() {

    pullRequestConversationFragment = PullRequestConversationFragment.newInstance(issueInfo);
    pullRequestConversationFragment.setPullRequestStoryLoaderInterface(this);

    pullRequestInfoFragment = PullRequestInfoFragment.newInstance(issueInfo);
    pullRequestFilesListFragment = PullRequestFilesListFragment.newInstance(issueInfo);
    pullRequestCommitsListFragment = PullRequestCommitsListFragment.newInstance(issueInfo);

    mBottomBar.setDefaultTabPosition(0);

    mBottomBar.setOnTabSelectListener(tabId -> {
      switch (tabId) {
        case R.id.tab_timeline:
          selectFragment(pullRequestConversationFragment);
          setToolbarColor(R.color.md_teal_800);
          break;
        case R.id.tab_info:
          selectFragment(pullRequestInfoFragment);
          setToolbarColor(R.color.md_amber_800);
          break;
        case R.id.tab_files:
          selectFragment(pullRequestFilesListFragment);
          setToolbarColor(R.color.md_brown_800);
          break;
        case R.id.tab_commits:
          selectFragment(pullRequestCommitsListFragment);
          setToolbarColor(R.color.md_deep_orange_800);
          break;
      }
    });
  }

  private void setToolbarColor(@ColorRes int color) {
    if (getToolbar() != null) {
      getToolbar().setBackgroundResource(color);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, color));
      }
    }
  }

  private void selectFragment(Fragment fragment) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getSimpleName());
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
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.clear();
    getMenuInflater().inflate(R.menu.pullrequest_detail, menu);
    MenuItem item = menu.findItem(R.id.share_issue);
    if (item != null) {
      IconicsDrawable drawable = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_share).actionBar().color(Color.WHITE);
      item.setIcon(drawable);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (story != null) {
      switch (item.getItemId()) {
        case R.id.share_issue:
          new ShareAction(this, story.item.title, story.item.getHtmlUrl()).setType("PullRequest").execute();
          break;
        case R.id.open_issue:
          new ViewInAction(this, story.item.getHtmlUrl()).setType("PullRequest").execute();
          break;
        case R.id.action_add_shortcut:
          ShortcutUtils.addPrShortcut(this, issueInfo);
          break;
      }
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onStoryLoaded(PullRequestStory story) {
    this.story = story;
    invalidateOptionsMenu();
    if (mBottomBar != null && story != null) {

      BottomBarTab tabFiles = mBottomBar.getTabWithId(R.id.tab_files);

      if (tabFiles != null) {
        tabFiles.setBadgeCount(story.item.changed_files);
      }

      BottomBarTab tabCommits = mBottomBar.getTabWithId(R.id.tab_commits);

      if (tabCommits != null) {
        tabCommits.setBadgeCount(story.item.commits);
      }

      if (pullRequestInfoFragment != null) {
        pullRequestInfoFragment.setArguments(PullRequestInfoFragment.newArguments(issueInfo, story.item));
      }
    }
  }
}
