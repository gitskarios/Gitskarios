package com.alorma.github.ui.fragment.issues;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BasePagerFragment;
import com.alorma.github.ui.fragment.base.NavigationPagerAdapter;
import com.alorma.github.ui.fragment.issues.users.AssignedIssuesListFragment;
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
  protected int getTitle() {
    return R.string.my_issues;
  }

  @Override
  protected PagerAdapter provideAdapter(FragmentManager fm) {
    List<Fragment> fragments = new ArrayList<>();
    fragments.add(AssignedIssuesListFragment.newInstance());

    List<String> titles = new ArrayList<>();
    titles.add(getString(R.string.user_assigned_issues));

    return new NavigationPagerAdapter(fm, fragments, titles);
  }
}
