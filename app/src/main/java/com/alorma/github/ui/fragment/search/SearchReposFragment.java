package com.alorma.github.ui.fragment.search;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.sdk.services.search.RepoSearchClient;
import com.alorma.github.ui.fragment.repos.BaseReposListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SearchReposFragment extends BaseReposListFragment implements TitleProvider {

  private String query = null;

  public static SearchReposFragment newInstance(String query) {
    Bundle args = new Bundle();
    if (query != null) {
      args.putString(SearchManager.QUERY, query);
    }
    SearchReposFragment f = new SearchReposFragment();
    f.setArguments(args);
    return f;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    String query = getArguments().getString(SearchManager.QUERY, null);
    if (query != null) {
      setQuery(query);
    } else {
      setEmpty();
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_results;
  }

  @Override
  protected void loadArguments() {

  }

  @Override
  protected void executeRequest() {
    if (getActivity() != null) {
      if (query != null) {
        super.executeRequest();
        RepoSearchClient client = new RepoSearchClient(query);
        client.observable()
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext((Action1<Pair<List<Repo>, Integer>>) listIntegerPair -> {
              if (getAdapter() != null) {
                getAdapter().clear();
              }
            })
            .doOnError((Action1<Throwable>) throwable -> {

            })
            .subscribe(this);
      }
    }
  }

  @Override
  protected void executePaginatedRequest(int page) {
    if (getActivity() != null) {
      if (query != null) {
        super.executePaginatedRequest(page);
        RepoSearchClient client = new RepoSearchClient(query, page);
        client.observable()
            .subscribeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this);
      }
    }
  }

  public void setQuery(String query) {
    this.query = query;
    executeRequest();
  }

  @Override
  public int getTitle() {
    return R.string.navigation_repos_search;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Search;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Search;
  }
}
