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
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.CompareCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.CompareCommitsClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.compare.CompareCommitsListFragment;
import com.alorma.github.ui.fragment.compare.CompareFilesListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by a557114 on 31/07/2015.
 */
public class CompareRepositoryCommitsActivity extends BackActivity implements BaseClient.OnResultCallback<CompareCommit> {

    private static final String REPO_INFO = "REPO_INFO";
    private static final String BASE = "BASE";
    private static final String HEAD = "HEAD";
    private List<Fragment> listFragments;
    private CompareCommitsListFragment commitsFragment;
    private CompareFilesListFragment filesFragment;

    public static Intent launcherIntent(Context context, RepoInfo repoInfo, String base, String head) {
        Intent intent = new Intent(context, CompareRepositoryCommitsActivity.class);

        intent.putExtra(REPO_INFO, repoInfo);
        intent.putExtra(BASE, base);
        intent.putExtra(HEAD, head);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_commits);

        if (getIntent().getExtras() != null) {
            RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
            String base = getIntent().getExtras().getString(BASE);
            String head = getIntent().getExtras().getString(HEAD);

            setTitle(base + " ... " + head);

            CompareCommitsClient compareCommitsClient = new CompareCommitsClient(this, repoInfo, base, head);
            compareCommitsClient.setOnResultCallback(this);
            compareCommitsClient.execute();

            TabLayout slidingTabLayout = (TabLayout) findViewById(R.id.tabStrip);

            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

            listFragments = new ArrayList<>();
            commitsFragment = CompareCommitsListFragment.newInstance(repoInfo);
            listFragments.add(commitsFragment);
            filesFragment = CompareFilesListFragment.newInstance(repoInfo);
            listFragments.add(filesFragment);
            NavigationPagerAdapter adapter = new NavigationPagerAdapter(getSupportFragmentManager(), listFragments);

            viewPager.setAdapter(adapter);
            slidingTabLayout.setupWithViewPager(viewPager);

        } else {
            finish();
        }
    }

    @Override
    public void onResponseOk(CompareCommit compareCommit, Response r) {
        if (commitsFragment != null) {
            commitsFragment.setCommits(compareCommit.commits);
        }
        if (filesFragment != null) {
            filesFragment.setFiles(compareCommit.files);
        }
    }

    @Override
    public void onFail(RetrofitError error) {

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
                    return getString(R.string.commits);
                case 1:
                    return getString(R.string.commits_detail_files);
            }
            return "";
        }
    }

}
