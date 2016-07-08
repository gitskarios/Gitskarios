package com.alorma.github.sdk.services.emojis;

import com.alorma.github.sdk.services.client.GithubClient;
import java.util.HashMap;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisClient extends GithubClient<HashMap<String, String>> {

  public EmojisClient() {
    super();
  }

  @Override
  protected Observable<HashMap<String, String>> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(EmojisService.class).getEmojis();
  }
}
