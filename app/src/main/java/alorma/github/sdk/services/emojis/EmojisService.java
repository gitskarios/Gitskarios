package com.alorma.github.sdk.services.emojis;

import java.util.HashMap;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by Bernat on 08/07/2015.
 */
public interface EmojisService {

  // Sync
  @GET("/emojis")
  Observable<HashMap<String, String>> getEmojis();
}
