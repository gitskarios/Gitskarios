package com.alorma.github.sdk.services.gtignore;

import com.alorma.github.sdk.bean.dto.response.GitIgnoreTemplates;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by Bernat on 28/09/2014.
 */
public interface GitIgnoreService {

  //Sync
  @GET("/gitignore/templates")
  Observable<GitIgnoreTemplates> list();
}
