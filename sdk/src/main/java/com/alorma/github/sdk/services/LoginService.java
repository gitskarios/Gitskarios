package com.alorma.github.sdk.services;

import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.bean.request.dto.RequestTokenDTO;

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
