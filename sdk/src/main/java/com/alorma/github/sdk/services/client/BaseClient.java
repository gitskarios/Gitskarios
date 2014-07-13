package com.alorma.github.sdk.services.client;

import android.content.Context;
import android.util.Log;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class BaseClient<K> implements Callback<K>, RequestInterceptor, RestAdapter.Log {

    private final StoreCredentials storeCredentials;
    private OnResultCallback<K> onResultCallback;

    public BaseClient(Context context) {
        storeCredentials = new StoreCredentials(context);
    }

    public void execute() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApiConstants.API_URL)
                .setRequestInterceptor(this)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setLog(this)
                .build();

        executeService(restAdapter);
    }

    protected abstract void executeService(RestAdapter restAdapter);

    @Override
    public void success(K k, Response response) {
        if (onResultCallback != null) {
            onResultCallback.onResponseOk(k, response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if (onResultCallback != null) {
            onResultCallback.onFail(error);
        }
    }

    public OnResultCallback<K> getOnResultCallback() {
        return onResultCallback;
    }

    public void setOnResultCallback(OnResultCallback<K> onResultCallback) {
        this.onResultCallback = onResultCallback;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Accept", "application/vnd.github.v3.full+json");
        request.addHeader("Authorization", "token " + storeCredentials.token());
    }

    @Override
    public void log(String message) {

    }

    public interface OnResultCallback<K> {
        void onResponseOk(K k, Response r);
        void onFail(RetrofitError error);
    }
}
