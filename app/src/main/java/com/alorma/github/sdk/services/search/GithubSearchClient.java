package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.services.client.GithubListClient;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Bernat on 08/08/2014.
 */
public abstract class GithubSearchClient<Search, K> extends GithubListClient<K> {
  protected String query;
  private int page = 0;

  public GithubSearchClient(String query) {
    super();
    this.query = query;
  }

  public GithubSearchClient(String query, int page) {
    super();
    this.query = query;
    this.page = page;
  }

  public int getPage() {
    return page;
  }

  public abstract class SearchCallback implements Callback<Search> {

    protected Callback<K> callback;

    public SearchCallback(Callback<K> callback) {

      this.callback = callback;
    }

    @Override
    public void failure(RetrofitError error) {
      callback.failure(error);
    }
  }
}
