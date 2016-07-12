package com.alorma.github.sdk.services.user;

import com.alorma.github.sdk.bean.dto.response.Email;
import com.alorma.github.sdk.services.client.GithubClient;
import java.util.List;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 12/07/2014.
 */
public class UserEmailsClient extends GithubClient<List<Email>> {
  public UserEmailsClient() {
    super();
  }

  @Override
  protected Observable<List<Email>> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(UsersService.class).userEmails();
  }
}
