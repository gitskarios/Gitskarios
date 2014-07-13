package com.alorma.github.sdk.services.login;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.request.RequestTokenDTO;
import com.alorma.github.sdk.bean.dto.response.Token;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.security.ApiConstants;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 13/07/2014.
 */
public class RequestTokenClient extends BaseClient<Token> {
    private String code;

    public RequestTokenClient(Context context, String code) {
        super(context);
        this.code = code;
    }

    @Override
    public void execute() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://github.com")
                .setRequestInterceptor(this)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        executeService(restAdapter);
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Accept", "application/json");
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        LoginService loginService = restAdapter.create(LoginService.class);

        RequestTokenDTO tokenDTO = new RequestTokenDTO();
        tokenDTO.client_id = ApiConstants.CLIENT_ID;
        tokenDTO.client_secret = ApiConstants.CLIENT_SECRET;
        tokenDTO.redirect_uri = ApiConstants.CLIENT_CALLBACK;
        tokenDTO.code = code;

        loginService.requestToken(tokenDTO, this);
    }
}
