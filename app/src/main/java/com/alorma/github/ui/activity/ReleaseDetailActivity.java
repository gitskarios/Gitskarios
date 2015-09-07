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
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.sdk.bean.info.ReleaseInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.orgs.OrgsMembersFragment;
import com.alorma.github.ui.fragment.orgs.OrgsReposFragment;
import com.alorma.github.ui.fragment.releases.ReleaseAboutFragment;
import com.alorma.github.ui.fragment.releases.ReleaseAssetsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 22/02/2015.
 */
public class ReleaseDetailActivity extends BackActivity {

    private static final String RELEASE_INFO = "RELEASE_INFO";
    private static final String RELEASE = "RELEASE";
    private static final String REPO_INFO = "REPO_INFO";

    public static Intent launchIntent(Context context, ReleaseInfo releaseInfo) {
        Intent intent = new Intent(context, ReleaseDetailActivity.class);

        Bundle extras = new Bundle();
        extras.putParcelable(RELEASE_INFO, releaseInfo);

        intent.putExtras(extras);

        return intent;
    }

    public static Intent launchIntent(Context context, Release release, RepoInfo repoInfo) {
        Intent intent = new Intent(context, ReleaseDetailActivity.class);

        Bundle extras = new Bundle();
        extras.putParcelable(RELEASE, release);
        extras.putParcelable(REPO_INFO, repoInfo);

        intent.putExtras(extras);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_detail_activity);

        if (getIntent().getExtras() != null) {

            if (getIntent().getExtras().containsKey(RELEASE)) {
                Release release = getIntent().getExtras().getParcelable(RELEASE);
                RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
                showRelease(release, repoInfo);
            } else if (getIntent().getExtras().containsKey(RELEASE_INFO)) {
                ReleaseInfo release = getIntent().getExtras().getParcelable(RELEASE_INFO);
                Toast.makeText(this, "Should load release", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showRelease(Release release, RepoInfo repoInfo) {
        String name = release.name;
        if (TextUtils.isEmpty(name)) {
            name = release.tag_name;
        }
        setTitle(name);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabStrip);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        List<Fragment> listFragments = new ArrayList<>();
        listFragments.add(ReleaseAboutFragment.newInstance(release, repoInfo));

        List<ReleaseAsset> assets = new ArrayList<>();

        assets.addAll(release.assets);

        ReleaseAsset zipAsset = new ReleaseAsset();
        zipAsset.name = getString(R.string.release_asset_zip);
        zipAsset.browser_download_url = release.zipball_url;
        assets.add(zipAsset);

        ReleaseAsset tarAsset = new ReleaseAsset();
        tarAsset.name = getString(R.string.release_asset_tar);
        tarAsset.browser_download_url = release.tarball_url;
        assets.add(tarAsset);

        listFragments.add(ReleaseAssetsFragment.newInstance(assets));

        viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));
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
                    return getString(R.string.repo_release_fragment_detail_title);
                case 1:
                    return getString(R.string.repo_release_fragment_assets_title);
            }
            return "";
        }
    }
}
