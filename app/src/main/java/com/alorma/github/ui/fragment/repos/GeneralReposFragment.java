package com.alorma.github.ui.fragment.repos;

import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BasePagerFragment;
import com.alorma.github.ui.fragment.base.NavigationPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class GeneralReposFragment extends BasePagerFragment {

  public static GeneralReposFragment newInstance() {
    return new GeneralReposFragment();
  }

  @StyleRes
  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @StyleRes
  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  @Override
  public int getTitle() {
    return R.string.navigation_general_repositories;
  }

  @Override
  protected PagerAdapter provideAdapter(FragmentManager fm) {
    List<Fragment> fragments = new ArrayList<>();
    fragments.add(CurrentAccountReposFragment.newInstance());
    fragments.add(StarredReposFragment.newInstance());
    fragments.add(WatchedReposFragment.newInstance());
    fragments.add(MembershipReposFragment.newInstance());
    fragments.add(ReposFragmentFromOrgs.newInstance());

    List<String> titles = new ArrayList<>();
    titles.add(getString(R.string.navigation_repos));
    titles.add(getString(R.string.navigation_starred_repos));
    titles.add(getString(R.string.navigation_watched_repos));
    titles.add(getString(R.string.navigation_member_repos));
    titles.add(getString(R.string.navigation_from_orgs));

    return new NavigationPagerAdapter(fm, fragments, titles);
  }
}
