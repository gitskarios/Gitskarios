package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by Bernat on 08/08/2014.
 */
public class RepoSearchClient extends GithubSearchClient<ReposSearch, List<Repo>> {

  public RepoSearchClient(String query) {
    super(query);
  }

  public RepoSearchClient(String query, int page) {
    super(query, page);
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        SearchClient searchClient = restAdapter.create(SearchClient.class);
        if (getPage() == 0) {
          searchClient.repos(query, new SearchReposCallback(this));
        } else {
          searchClient.repos(query, getPage(), new SearchReposCallback(this));
        }
      }
    };
  }

  private class SearchReposCallback extends SearchCallback {

    public SearchReposCallback(Callback<List<Repo>> callback) {
      super(callback);
    }

    @Override
    public void success(ReposSearch reposSearch, Response response) {
      callback.success(reposSearch.items, response);
    }
  }
}
