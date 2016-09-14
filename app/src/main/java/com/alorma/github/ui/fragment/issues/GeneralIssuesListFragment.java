package com.alorma.github.ui.fragment.issues;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BasePagerFragment;
import com.alorma.github.ui.fragment.base.NavigationPagerAdapter;
import com.alorma.github.ui.fragment.issues.user.AssignedIssuesListFragment;
import com.alorma.github.ui.fragment.issues.user.CreatedIssuesListFragment;
import com.alorma.github.ui.fragment.issues.user.MentionedIssuesListFragment;
import java.util.ArrayList;
import java.util.List;

public class GeneralIssuesListFragment extends BasePagerFragment {

  public static GeneralIssuesListFragment newInstance() {
    return new GeneralIssuesListFragment();
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public int getTitle() {
    return R.string.navigation_issues;
  }

  @Override
  protected PagerAdapter provideAdapter(FragmentManager fm) {
    List<Fragment> fragments = new ArrayList<>();
    fragments.add(AssignedIssuesListFragment.newInstance());
    fragments.add(CreatedIssuesListFragment.newInstance());
    fragments.add(MentionedIssuesListFragment.newInstance());
    //fragments.add(SubscribedIssuesListFragment.newInstance());

    List<String> titles = new ArrayList<>();
    titles.add(getString(R.string.user_assigned_issues));
    titles.add(getString(R.string.user_created_issues));
    titles.add(getString(R.string.user_mentioned_issues));
    //titles.add(getString(R.string.user_subscribed_issues));

    return new NavigationPagerAdapter(fm, fragments, titles);
  }
}
