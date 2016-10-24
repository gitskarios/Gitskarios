package com.alorma.github.ui.activity.repo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.fragment.commit.CommitsListFragment;
import com.alorma.github.ui.fragment.detail.repo.SourceListFragment;
import com.alorma.github.ui.fragment.releases.RepositoryTagsFragment;

public class RepoContentFragment extends BaseFragment {

  private static final String REPO_INFO = "REPO_INFO";
  @BindView(R.id.tabLayout) TabLayout tabLayout;

  public static RepoContentFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    RepoContentFragment f = new RepoContentFragment();
    f.setArguments(bundle);
    return f;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.repo_content_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    RepoInfo repoInfo = getArguments().getParcelable(REPO_INFO);

    if (repoInfo != null) {
      TabLayout.Tab tabCode = tabLayout.newTab().setText("Code").setTag(R.id.tab_code);
      TabLayout.Tab tabCommits = tabLayout.newTab().setText("Commits").setTag(R.id.tab_commits);
      TabLayout.Tab tabRelease = tabLayout.newTab().setText("Releases").setTag(R.id.tab_releases);

      tabLayout.addTab(tabCode);
      tabLayout.addTab(tabCommits);
      tabLayout.addTab(tabRelease);

      tabLayout.addOnTabSelectedListener(new TabListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
          if (tab.getTag() != null && tab.getTag() instanceof Integer) {
            int id = (int) tab.getTag();
            switch (id) {
              case R.id.tab_code:
                setFragment(SourceListFragment.newInstance(repoInfo));
                break;
              case R.id.tab_commits:
                setFragment(CommitsListFragment.newInstance(repoInfo));
                break;
              case R.id.tab_releases:
                setFragment(RepositoryTagsFragment.newInstance(repoInfo));
                break;
            }
          }
        }
      });

      tabCode.select();
      setFragment(SourceListFragment.newInstance(repoInfo));
    }
  }

  private void setFragment(Fragment fragment) {
    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
    ft.replace(R.id.content_layout, fragment);
    ft.commit();
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Repository;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Repository;
  }

  private abstract class TabListener implements TabLayout.OnTabSelectedListener {

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
  }
}
