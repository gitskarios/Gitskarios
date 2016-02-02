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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    setAction(new GetRepoContributorsClient(repoInfo));
  }

  protected void executePaginatedRequest(int page) {
    setAction(new GetRepoContributorsClient(repoInfo, page));
  }

  private void setAction(GithubClient<List<Contributor>> client) {
    startRefresh();
    client.observable().subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<List<Contributor>, List<User>>() {
          @Override
          public List<User> call(List<Contributor> contributors) {
            List<User> users = new ArrayList<User>();
            for (Contributor contributor : contributors) {
              users.add(contributor.author);
            }
            return users;
          }
        })
        .subscribe(new Subscriber<List<User>>() {
          @Override
          public void onCompleted() {
            stopRefresh();
          }

          @Override
          public void onError(Throwable e) {
            stopRefresh();
            if (getAdapter() == null || getAdapter().getItemCount() == 0) {
              setEmpty();
            }
          }

          @Override
          public void onNext(List<User> users) {
            if (getActivity() != null) {
              if (users.size() > 0) {
                hideEmpty();
                if (refreshing || getAdapter() == null) {
                  UsersAdapter adapter = new UsersAdapter(LayoutInflater.from(getActivity()));
                  adapter.addAll(users);
                  setAdapter(adapter);
                } else {
                  getAdapter().addAll(users);
                }
              } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
                setEmpty();
              } else {
                getAdapter().clear();
                setEmpty();
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

  protected void loadArguments() {
    if (getArguments() != null) {
      repoInfo = (RepoInfo) getArguments().getParcelable(REPO_INFO);
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
