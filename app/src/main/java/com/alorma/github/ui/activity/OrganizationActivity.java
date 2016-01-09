package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrgsMembersFragment;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 22/02/2015.
 */
public class OrganizationActivity extends BackActivity {

    private static final String ORG = "ORG";
    private ViewPager viewPager;

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

        viewPager = (ViewPager) findViewById(R.id.pager);

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

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));
        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewPager);
        } else {
            tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    tabLayout.setupWithViewPager(viewPager);

                    tabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }
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
