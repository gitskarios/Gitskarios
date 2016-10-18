package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
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
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.ShortcutUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabClickListener;
import java.util.ArrayList;
import java.util.List;

public class PullRequestDetailActivity extends RepositoryThemeActivity
    implements PullRequestConversationFragment.PullRequestStoryLoaderInterface {

  public static final String ISSUE_INFO_REPO_NAME = "ISSUE_INFO_REPO_NAME";
  public static final String ISSUE_INFO_REPO_OWNER = "ISSUE_INFO_REPO_OWNER";
  public static final String ISSUE_INFO_NUMBER = "ISSUE_INFO_NUMBER";

  private IssueInfo issueInfo;
  private BottomBar mBottomBar;
  private BottomBarBadge badgeFiles;
  private BottomBarBadge badgeCommits;
  private PullRequestInfoFragment infoFragment;
  private PullRequestStory story;

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
    setContentView(R.layout.generic_toolbar_coordinator);

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

      createBottom(savedInstanceState);
    }
  }

  private void createBottom(Bundle savedInstanceState) {
    mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.coordinator), findViewById(R.id.content), savedInstanceState);
    mBottomBar.useOnlyStatusBarTopOffset();

    mBottomBar.noTabletGoodness();

    final List<Fragment> fragments = new ArrayList<>();

    PullRequestConversationFragment overviewFragment = PullRequestConversationFragment.newInstance(issueInfo);
    overviewFragment.setPullRequestStoryLoaderInterface(this);

    fragments.add(overviewFragment);

    infoFragment = PullRequestInfoFragment.newInstance(issueInfo);
    fragments.add(infoFragment);

    fragments.add(PullRequestFilesListFragment.newInstance(issueInfo));
    fragments.add(PullRequestCommitsListFragment.newInstance(issueInfo));

    mBottomBar.setItems(new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_comment_discussion), "Conversation"),
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_info), "Info"),
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_file_code), "Files"),
        new BottomBarTab(getBottomTabIcon(Octicons.Icon.oct_git_commit), "Commits"));

    mBottomBar.setDefaultTabPosition(0);

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

    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    String pref_theme = defaultSharedPreferences.getString("pref_theme", getString(R.string.theme_light));
    if ("theme_dark".equalsIgnoreCase(pref_theme)) {
      mBottomBar.useDarkTheme();
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
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mBottomBar.onSaveInstanceState(outState);
  }

  @Override
  public void onStoryLoaded(PullRequestStory story) {
    this.story = story;
    invalidateOptionsMenu();
    if (mBottomBar != null && story != null) {
      if (badgeFiles == null) {
        badgeFiles = mBottomBar.makeBadgeForTabAt(2, AttributesUtils.getAccentColor(this), story.item.changed_files);
      }
      badgeFiles.setCount(story.item.changed_files);
      badgeFiles.setAutoShowAfterUnSelection(true);

      if (badgeCommits == null) {
        badgeCommits = mBottomBar.makeBadgeForTabAt(3, AttributesUtils.getAccentColor(this), story.item.commits);
      }
      badgeCommits.setCount(story.item.commits);
      badgeCommits.setAutoShowAfterUnSelection(true);

      if (infoFragment != null) {
        infoFragment.setArguments(PullRequestInfoFragment.newArguments(issueInfo, story.item));
      }
    }
  }
}
