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
import com.alorma.github.sdk.bean.dto.response.CompareCommit;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.CompareCommitsClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.compare.CompareCommitsListFragment;
import com.alorma.github.ui.fragment.compare.CompareFilesListFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by a557114 on 31/07/2015.
 */
public class CompareRepositoryCommitsActivity extends BackActivity {

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
            RepoInfo repoInfo = (RepoInfo) getIntent().getExtras().getParcelable(REPO_INFO);
            String base = getIntent().getExtras().getString(BASE);
            String head = getIntent().getExtras().getString(HEAD);

            setTitle(base + " ... " + head);

            CompareCommitsClient compareCommitsClient = new CompareCommitsClient(repoInfo, base, head);
            compareCommitsClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<CompareCommit>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(CompareCommit compareCommit) {
                    if (commitsFragment != null) {
                        commitsFragment.setCommits(compareCommit.commits);
                    }
                    if (filesFragment != null) {
                        filesFragment.setFiles(compareCommit.files);
                    }
                }
            });

            final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabStrip);

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

            listFragments = new ArrayList<>();
            commitsFragment = CompareCommitsListFragment.newInstance(repoInfo);
            listFragments.add(commitsFragment);
            filesFragment = CompareFilesListFragment.newInstance(repoInfo);
            listFragments.add(filesFragment);
            NavigationPagerAdapter adapter = new NavigationPagerAdapter(getSupportFragmentManager(), listFragments);

            viewPager.setAdapter(adapter);
            if (ViewCompat.isLaidOut(tabLayout)) {
                tabLayout.setupWithViewPager(viewPager);
            } else {
                tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                               int oldBottom) {
                        tabLayout.setupWithViewPager(viewPager);

                        tabLayout.removeOnLayoutChangeListener(this);
                    }
                });
            }
        } else {
            finish();
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
                    return getString(R.string.commits);
                case 1:
                    return getString(R.string.commits_detail_files);
            }
            return "";
        }
    }
}
