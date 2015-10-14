package com.alorma.gitskarios.core.client;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.alorma.gitskarios.core.ApiClient;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Response;
import retrofit.converter.Converter;

public abstract class BaseClient<K> implements Callback<K>, RequestInterceptor, RestAdapter.Log {

    private StoreCredentials storeCredentials;

    protected Context context;
    private OnResultCallback<K> onResultCallback;
    protected Handler handler;
    private ApiClient client;

    public Uri last;
    public Uri next;
    public int lastPage;
    public int nextPage;

    public BaseClient(Context context, ApiClient client) {
        this.client = client;
        if (context != null) {
            this.context = context.getApplicationContext();
        }
        storeCredentials = new StoreCredentials(context);
    }

    private RestAdapter getRestAdapter() {
        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder()
                .setEndpoint(client.getApiEndpoint())
                .setRequestInterceptor(this)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(this);

        if (customConverter() != null) {
            restAdapterBuilder.setConverter(customConverter());
        }

        if (getInterceptor() != null) {
            restAdapterBuilder.setClient(getInterceptor());
        }

        return restAdapterBuilder.build();
    }

    @Nullable
    protected Client getInterceptor() {
        return null;
    }

    public void execute() {
        try {
            handler = new Handler();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getToken() != null) {
            executeService(getRestAdapter());
        }
    }

    public K executeSync() {
        if (getToken() != null) {
            return executeServiceSync(getRestAdapter());
        }
        return null;
    }

    protected Converter customConverter() {
        return null;
    }

    protected abstract void executeService(RestAdapter restAdapter);

    protected abstract K executeServiceSync(RestAdapter restAdapter);

    @Override
    public void success(final K k, final Response response) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sendResponse(k, response);
                }
            });
        } else {
            sendResponse(k, response);
        }
    }

    private void sendResponse(K k, Response response) {
        if (onResultCallback != null) {
            onResultCallback.onResponseOk(k, response);
        }
    }

    @Override
    public void failure(final RetrofitError error) {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sendError(error);
                }
            });
        } else {
            sendError(error);
        }
    }

    private void sendError(RetrofitError error) {
        if (error.getResponse() != null && error.getResponse().getStatus() == 401) {
            if (context != null) {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
                manager.sendBroadcast(new UnAuthIntent(storeCredentials.token()));
            }
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


    protected String getToken() {
        return storeCredentials.token();
    }

    public Context getContext() {
        return context;
    }

    public interface OnResultCallback<K> {
        void onResponseOk(K k, Response r);

        void onFail(RetrofitError error);
    }

    public ApiClient getClient() {
        return client;
    }

    public void setStoreCredentials(StoreCredentials storeCredentials) {
        this.storeCredentials = storeCredentials;
    }
}
