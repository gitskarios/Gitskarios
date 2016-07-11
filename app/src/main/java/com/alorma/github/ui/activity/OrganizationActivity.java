package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrgsMembersFragment;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;
import java.util.ArrayList;
import java.util.List;

public class OrganizationActivity extends BackActivity {

  private static final String ORG = "ORG";

  public static Intent launchIntent(Context context, String orgName) {
    Intent intent = new Intent(context, OrganizationActivity.class);

    Bundle extras = new Bundle();
    extras.putString(ORG, orgName);

    intent.putExtras(extras);

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.organization_activity);

    setTitle(R.string.navigation_people);

    final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabStrip);

    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

    String orgName = null;
    if (getIntent().getExtras() != null) {
      orgName = getIntent().getExtras().getString(ORG);
    }

    setTitle(orgName);

    OrgsReposFragment orgReposFragment = OrgsReposFragment.newInstance(orgName);
    //OrgsTeamsFragment orgsTeamsFragment = OrgsTeamsFragment.newInstance(orgName);
    OrgsMembersFragment orgMembersFragment = OrgsMembersFragment.newInstance(orgName);

    ArrayList<Fragment> listFragments = new ArrayList<>();
    listFragments.add(orgReposFragment);
    //listFragments.add(orgsTeamsFragment);
    listFragments.add(orgMembersFragment);

    if (viewPager != null && tabLayout != null) {
      viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));
      tabLayout.setupWithViewPager(viewPager);
    }
  }

  @Override
  protected int getAppDarkTheme() {
    return R.style.AppTheme_Dark_People;
  }

  @Override
  protected int getAppLightTheme() {
    return R.style.AppTheme_People;
  }

  private class NavigationPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> listFragments;

    public NavigationPagerAdapter(FragmentManager fm, List<Fragment> listFragments) {
      super(fm);
      this.listFragments = listFragments;
    }

    @Override
    public Fragment getItem(int position) {
      return listFragments.get(position);
    }

    @Override
    public int getCount() {
      return listFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      switch (position) {
        case 0:
          return getString(R.string.navigation_orgs_repos);/*
                case 1:
					return getString(R.string.navigation_orgs_teams);
				case 2:
					return getString(R.string.navigation_orgs_members);*/
        case 1:
          return getString(R.string.navigation_orgs_members);
      }
      return "";
    }
  }
}
