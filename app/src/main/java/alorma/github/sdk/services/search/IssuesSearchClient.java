package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.search.IssuesSearch;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class IssuesSearchClient extends GithubSearchClient<IssuesSearch, List<Issue>> {

  public IssuesSearchClient(String query) {
    super(query);
  }

  public IssuesSearchClient(String query, int page) {
    super(query, page);
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {
      @Override
      protected void call(RestAdapter restAdapter) {
        SearchClient searchClient = restAdapter.create(SearchClient.class);
        if (getPage() == 0) {
          searchClient.issues(query, new SearchIssuesCallback(this));
        } else {
          searchClient.issues(query, getPage(), new SearchIssuesCallback(this));
        }
      }
    };
  }

  private class SearchIssuesCallback extends SearchCallback {

    public SearchIssuesCallback(Callback<List<Issue>> callback) {
      super(callback);
    }

    @Override
    public void success(IssuesSearch issuesSearch, Response response) {
      callback.success(issuesSearch.items, response);
    }
  }
}
