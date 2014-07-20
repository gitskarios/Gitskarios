package com.alorma.github.sdk.services.login;

import com.alorma.github.sdk.bean.dto.request.RequestTokenDTO;
import com.alorma.github.sdk.bean.dto.response.Token;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Bernat on 13/07/2014.
 */
public interface LoginService {

    @POST("/login/oauth/access_token")
    void requestToken(@Body RequestTokenDTO requestTokenDTO, Callback<Token> callback);

}
