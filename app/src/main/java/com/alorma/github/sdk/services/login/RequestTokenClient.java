package com.alorma.github.sdk.services.login;

import com.alorma.github.sdk.bean.dto.request.RequestTokenDTO;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.services.client.GithubClient;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 13/07/2014.
 */
public class RequestTokenClient extends GithubClient<Token> {
  private final String clientId;
  private final String clientSecret;
  private final String redirectUri;
  private String code;

  public RequestTokenClient(String code, String clientId, String clientSecret, String redirectUri) {
    super();
    this.code = code;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
  }

  @Override
  protected RestAdapter getRestAdapter() {

    return new RestAdapter.Builder().setEndpoint(getClient().getApiOauthUrlEndpoint())
        .setRequestInterceptor(this)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .build();
  }

  @Override
  public void intercept(RequestFacade request) {
    request.addHeader("Accept", "application/json");
  }

  @Override
  protected Observable<Token> getApiObservable(RestAdapter restAdapter) {
    LoginService loginService = restAdapter.create(LoginService.class);

    RequestTokenDTO tokenDTO = new RequestTokenDTO();
    tokenDTO.client_id = clientId;
    tokenDTO.client_secret = clientSecret;
    tokenDTO.redirect_uri = redirectUri;
    tokenDTO.code = code;

    return loginService.requestToken(tokenDTO);
  }
}
