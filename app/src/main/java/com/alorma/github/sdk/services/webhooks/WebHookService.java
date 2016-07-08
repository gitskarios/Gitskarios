package com.alorma.github.sdk.services.webhooks;

import com.alorma.github.sdk.bean.dto.request.WebHookRequest;
import com.alorma.github.sdk.bean.dto.request.WebHookResponse;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

public interface WebHookService {
  @POST("/repos/{owner}/{name}/hooks")
  Observable<WebHookResponse> addWebHook(@Path("owner") String owner, @Path("name") String repo,
      @Body WebHookRequest webHookRequest);
}