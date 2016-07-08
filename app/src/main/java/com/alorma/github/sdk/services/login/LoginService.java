package com.alorma.github.sdk.services.login;

import com.alorma.github.sdk.bean.dto.request.CreateAuthorization;
import com.alorma.github.sdk.bean.dto.request.RequestTokenDTO;
import com.alorma.github.sdk.bean.dto.response.GithubAuthorization;
import com.alorma.github.sdk.bean.dto.response.Token;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface LoginService {

  @POST("/login/oauth/access_token")
  Observable<Token> requestToken(@Body RequestTokenDTO requestTokenDTO);

  @POST("/authorizations")
  Observable<GithubAuthorization> createAuthorization(
      @Body CreateAuthorization createAuthorization);
}
