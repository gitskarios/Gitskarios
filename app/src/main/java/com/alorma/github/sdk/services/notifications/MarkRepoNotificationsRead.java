package com.alorma.github.sdk.services.notifications;

import com.alorma.github.sdk.bean.dto.request.LastDate;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Bernat on 01/03/2015.
 */
public class MarkRepoNotificationsRead extends GithubClient<Boolean> {
  private RepoInfo repoInfo;

  public MarkRepoNotificationsRead(RepoInfo repoInfo) {
    super();
    this.repoInfo = repoInfo;
  }

  @Override
  protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
    DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC);
    String date = ISODateTimeFormat.dateTime().print(dateTime);
    LastDate lastDate = new LastDate(date);
    return restAdapter.create(NotificationsService.class)
        .markAsReadRepo(repoInfo.owner, repoInfo.name, lastDate)
        .map(new Func1<Response, Boolean>() {
          @Override
          public Boolean call(Response response) {
            return response != null && response.getStatus() == 204;
          }
        });
  }

  @Override
  public String getAcceptHeader() {
    return "application/json";
  }
}
