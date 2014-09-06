package com.alorma.github.sdk.services.client;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;
import com.alorma.github.sdk.security.UnAuthIntent;

import java.net.Proxy;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public abstract class BaseClient<K> implements Callback<K>, RequestInterceptor, RestAdapter.Log {

    private final StoreCredentials storeCredentials;
    protected final Context context;
    private OnResultCallback<K> onResultCallback;

    public BaseClient(Context context) {
        this.context = context;
        storeCredentials = new StoreCredentials(context);
    }

    public void execute() {

        RestAdapter restAdapter = new RestAdapter.Builder()
				.setClient(new OkClient())
                .setEndpoint(ApiConstants.API_URL)
                .setRequestInterceptor(this)
                .setLogLevel(RestAdapter.LogLevel.FULL)
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
        if (error.getResponse() != null && error.getResponse().getStatus() == 401) {
            storeCredentials.clear();
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
            manager.sendBroadcast(new UnAuthIntent());
        } else {
            if (onResultCallback != null) {
                onResultCallback.onFail(error);
            }
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
        request.addHeader("Accept", getAcceptHeader());
        request.addHeader("Authorization", "token " + storeCredentials.token());
    }

    @Override
    public void log(String message) {
        Log.v("RETROFIT_LOG", message);
    }

    public String getAcceptHeader() {
        return "application/vnd.github.v3.full+json";
    }

    public interface OnResultCallback<K> {
        void onResponseOk(K k, Response r);

        void onFail(RetrofitError error);
    }
}
