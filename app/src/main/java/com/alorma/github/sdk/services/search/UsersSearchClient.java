package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.search.UsersSearch;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by Bernat on 08/08/2014.
 */
public class UsersSearchClient extends GithubSearchClient<UsersSearch, List<User>> {

  public UsersSearchClient(String query) {
    super(query);
  }

  public UsersSearchClient(String query, int page) {
    super(query, page);
  }

  @Override
  protected ApiSubscriber getApiObservable(RestAdapter restAdapter) {
    return new ApiSubscriber() {

      @Override
      protected void call(RestAdapter restAdapter) {
        SearchClient searchClient = restAdapter.create(SearchClient.class);
        if (getPage() == 0) {
          searchClient.users(query, new SearchUsersCallback(this));
        } else {
          searchClient.users(query, getPage(), new SearchUsersCallback(this));
        }
      }
    };
  }

  private class SearchUsersCallback extends SearchCallback {

    public SearchUsersCallback(Callback<List<User>> callback) {
      super(callback);
    }

    @Override
    public void success(UsersSearch usersSearch, Response response) {
      callback.success(usersSearch.items, response);
    }
  }
}
