package com.alorma.github.ui.fragment.detail.repo;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class RepoContributorsFragment extends LoadingListFragment<UsersAdapter> implements TitleProvider {

  private static final String REPO_INFO = "REPO_INFO";
  private RepoInfo repoInfo;

  public static RepoContributorsFragment newInstance(RepoInfo repoInfo) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, repoInfo);

    RepoContributorsFragment fragment = new RepoContributorsFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  protected void executeRequest() {
    setAction(new GetRepoContributorsClient(getActivity(), repoInfo));
  }

  protected void executePaginatedRequest(int page) {
    setAction(new GetRepoContributorsClient(getActivity(), repoInfo, page));
  }

  private void setAction(GithubClient<List<Contributor>> client) {
    client.observable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Contributor>>() {
      @Override
      public void onCompleted() {
        stopRefresh();
      }

      @Override
      public void onError(Throwable e) {
        stopRefresh();
        if (getAdapter() == null || getAdapter().getItemCount() == 0) {
          setEmpty(true);
        }
      }

      @Override
      public void onNext(List<Contributor> contributors) {
        if (contributors != null) {
          List<User> users = new ArrayList<>();

          for (Contributor contributor : contributors) {
            if (contributor != null
                && contributor.author != null
                && contributor.author.login != null
                && !contributor.author.login.equalsIgnoreCase(repoInfo.owner)) {
              users.add(users.size(), contributor.author);
            }
          }

          if (getAdapter() == null) {
            UsersAdapter adapter = new UsersAdapter(LayoutInflater.from(getActivity()));
            adapter.addAll(users);
            setAdapter(adapter);
          } else {
            getAdapter().addAll(users);
          }
        }
      }
    });
  }

  @Override
  public int getTitle() {
    return R.string.contributors_fragment_title;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_person;
  }

  @Override
  protected RecyclerView.LayoutManager getLayoutManager() {
    return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.grid_layout_columns));
  }

  @Override
  protected RecyclerView.ItemDecoration getItemDecoration() {
    return null;
  }

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = getArguments().getParcelable(REPO_INFO);
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_person;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_contributors;
  }
}
