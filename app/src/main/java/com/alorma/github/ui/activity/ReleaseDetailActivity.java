package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.sdk.bean.info.ReleaseInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetReleaseClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.viewpager.NavigationPagerAdapter;
import com.alorma.github.ui.fragment.releases.ReleaseAboutFragment;
import com.alorma.github.ui.fragment.releases.ReleaseAssetsFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 22/02/2015.
 */
public class ReleaseDetailActivity extends BackActivity implements Observer<Release> {

    private static final String RELEASE_INFO = "RELEASE_INFO";
    private static final String RELEASE = "RELEASE";
    private static final String REPO_INFO = "REPO_INFO";
    private ReleaseInfo releaseInfo;
    private TabLayout tabLayout;
    private ViewPager viewPager;

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

            tabLayout = (TabLayout) findViewById(R.id.tabStrip);
            viewPager = (ViewPager) findViewById(R.id.pager);

            if (getIntent().getExtras().containsKey(RELEASE)) {
                Release release = (Release) getIntent().getExtras().getParcelable(RELEASE);
                RepoInfo repoInfo = (RepoInfo) getIntent().getExtras().getParcelable(REPO_INFO);
                showRelease(release, repoInfo);
            } else if (getIntent().getExtras().containsKey(RELEASE_INFO)) {
                releaseInfo = (ReleaseInfo) getIntent().getExtras().getParcelable(RELEASE_INFO);
                GetReleaseClient releaseClient = new GetReleaseClient(releaseInfo);
                releaseClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this);
            }
        }
    }

    private void showRelease(Release release, RepoInfo repoInfo) {
        if (release != null) {
            String name = release.name;
            if (TextUtils.isEmpty(name)) {
                name = release.tag_name;
            }
            setTitle(name);

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

            if (viewPager != null) {
                viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), getResources(), listFragments));
                if (tabLayout != null) {
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
        }
    }

    @Override
    public void onNext(Release release) {
        showRelease(release, releaseInfo.repoInfo);
    }

    @Override
    public void onError(Throwable error) {

    }

    @Override
    public void onCompleted() {

    }
}
