package com.alorma.github.ui.fragment.gists;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.gists.UserStarredGistsClient;
import com.alorma.github.ui.activity.gists.GistDetailActivity;
import com.alorma.github.ui.adapter.GistsAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.github.ui.fragment.detail.repo.BackManager;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AuthUserStarredGistsFragment extends LoadingListFragment<GistsAdapter>
    implements GistsAdapter.GistsAdapterListener, BackManager {

  public static AuthUserStarredGistsFragment newInstance() {
    return new AuthUserStarredGistsFragment();
  }

  @Override
  protected void loadArguments() {

  }

  @Override
  public void onResume() {
    super.onResume();

    getActivity().setTitle(R.string.navigation_gists_starred);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    GistsAdapter adapter = new GistsAdapter(LayoutInflater.from(getActivity()));
    adapter.setGistsAdapterListener(this);
    setAdapter(adapter);
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_gist;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_gists;
  }

  @Override
  protected void executeRequest() {
    super.executeRequest();

    setAction(new UserStarredGistsClient());
  }

  @Override
  protected void executePaginatedRequest(int page) {
    super.executePaginatedRequest(page);

    setAction(new UserStarredGistsClient(page));
  }

  private void setAction(GithubListClient<List<Gist>> userGistsClient) {
    userGistsClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<Pair<List<Gist>, Integer>, List<Gist>>() {
          @Override
          public List<Gist> call(Pair<List<Gist>, Integer> listIntegerPair) {
            setPage(listIntegerPair.second);
            return listIntegerPair.first;
          }
        })
        .subscribe(new Subscriber<List<Gist>>() {
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
          public void onNext(List<Gist> gists) {
            if (refreshing) {
              getAdapter().clear();
            }
            getAdapter().addAll(gists);
          }
        });
  }

  @Override
  public void onGistSelected(Gist gist) {
    Intent launcherIntent = GistDetailActivity.createLauncherIntent(getActivity(), gist.id);
    startActivity(launcherIntent);
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }
}