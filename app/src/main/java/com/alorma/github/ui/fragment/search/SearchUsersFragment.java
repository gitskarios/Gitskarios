package com.alorma.github.ui.fragment.search;

import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.sdk.services.search.UsersSearchClient;
import com.alorma.github.ui.adapter.users.UsersAdapter;
import com.alorma.github.ui.fragment.users.BaseUsersListFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.User;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchUsersFragment extends BaseUsersListFragment implements TitleProvider {

  private String query;

  public static SearchUsersFragment newInstance(String query) {
    Bundle args = new Bundle();
    if (query != null) {
      args.putString(SearchManager.QUERY, query);
    }
    SearchUsersFragment f = new SearchUsersFragment();
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

  public void setQuery(String query) {
    this.query = query;
    executeRequest();
  }

  @Override
  protected void loadArguments() {

  }

  @Override
  protected void executeRequest() {
    if (getActivity() != null) {
      if (query != null) {
        super.executeRequest();
        UsersSearchClient client = new UsersSearchClient(query);
        client.observable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(o -> onUsersLoaded(o, true), Throwable::printStackTrace);
        query = null;
        if (getAdapter() != null) {
          getAdapter().clear();
        }
      }
    }
  }

  private void onUsersLoaded(Pair<List<User>, Integer> pair, boolean refresh) {
    setPage(pair.second);
    List<User> users = pair.first;

    if (users.size() > 0) {
      hideEmpty();
      if (refresh || getAdapter() == null) {
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
    stopRefresh();
  }

  @Override
  protected void executePaginatedRequest(int page) {
    if (getActivity() != null) {
      if (query != null) {
        super.executePaginatedRequest(page);
        UsersSearchClient client = new UsersSearchClient(query, page);
        client.observable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(o -> onUsersLoaded(o, false), Throwable::printStackTrace);
        query = null;
        if (getAdapter() != null) {
          getAdapter().clear();
        }
      }
    }
  }

  @Override
  protected Octicons.Icon getNoDataIcon() {
    return Octicons.Icon.oct_person;
  }

  @Override
  protected int getNoDataText() {
    return R.string.no_results;
  }

  @Override
  public int getTitle() {
    return R.string.navigation_people;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_person;
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
