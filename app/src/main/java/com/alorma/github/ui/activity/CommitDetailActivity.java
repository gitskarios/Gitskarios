package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.commit.GetSingleCommitClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.commit.CommitCommentsFragment;
import com.alorma.github.ui.fragment.commit.CommitFilesFragment;
import com.alorma.github.ui.view.GitChangeStatusColorsView;
import com.alorma.github.ui.view.SlidingTabLayout;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.gitskarios.basesdk.client.BaseClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitDetailActivity extends BackActivity implements CommitFilesAdapter.OnFileRequestListener, BaseClient.OnResultCallback<Commit> {

    private CommitInfo info;

    private List<Fragment> listFragments;
    private CommitFilesFragment commitFilesFragment;

    public static Intent launchIntent(Context context, CommitInfo commitInfo) {
        Bundle b = new Bundle();
        b.putParcelable(CommitFilesFragment.INFO, commitInfo);

        Intent intent = new Intent(context, CommitDetailActivity.class);
        intent.putExtras(b);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commit_activity);

        if (getIntent().getExtras() != null) {
            info = getIntent().getExtras().getParcelable(CommitFilesFragment.INFO);

            setTitle(String.valueOf(info.repoInfo));

            getContent();

            SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabStrip);
            ViewCompat.setElevation(slidingTabLayout, 4);

            slidingTabLayout.setSelectedIndicatorColors(AttributesUtils.getAccentColor(this));
            slidingTabLayout.setDividerColors(Color.TRANSPARENT);

            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

            listFragments = new ArrayList<>();

            commitFilesFragment = CommitFilesFragment.newInstance(info);
            commitFilesFragment.setOnFileRequestListener(this);
            listFragments.add(commitFilesFragment);

            CommitCommentsFragment commitCommentsFragment = CommitCommentsFragment.newInstance(info);
            listFragments.add(commitCommentsFragment);

            viewPager.setAdapter(new NavigationPagerAdapter(getSupportFragmentManager(), listFragments));
            slidingTabLayout.setViewPager(viewPager);

        }
    }

    @Override
    protected void getContent() {
        super.getContent();
        GetSingleCommitClient client = new GetSingleCommitClient(this, info);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    public void onResponseOk(Commit commit, Response r) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(commit.shortSha());
        }

        if (commitFilesFragment != null) {
            commitFilesFragment.setFiles(commit.files);
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    @Override
    public void onFileRequest(CommitFile file) {
        FileInfo info = new FileInfo();
        info.content = file.patch;
        info.name = file.getFileName();
        Intent launcherIntent = FileActivity.createLauncherIntent(this, info, false);
        startActivity(launcherIntent);
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
                    return getString(R.string.commits_detail_files);
                case 1:
                    return getString(R.string.commits_detail_comments);
            }
            return "";
        }
    }

}
