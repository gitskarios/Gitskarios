package com.alorma.github.sdk.services.client;

import com.google.gson.Gson;

import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.security.StoreCredentials;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class SyncBaseClient implements RequestInterceptor, RestAdapter.Log {

    protected static final Gson GSON = new Gson();

    private final StoreCredentials mStoreCredentials;

    protected final RestAdapter mRestAdapter;

    public SyncBaseClient(final Context context) {
        mStoreCredentials = new StoreCredentials(context);

        mRestAdapter = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setEndpoint(ApiConstants.API_URL)
                .setRequestInterceptor(this)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(this)
                .build();
    }

    protected static <T> T getObjectFromResponse(final Response response,
            final Class<T> clazz) throws IOException {
        final InputStream in = response.getBody().in();
        final BufferedReader json = new BufferedReader(new InputStreamReader(in));
        return GSON.fromJson(json, clazz);
    }

    protected static PaginationLink getLinkData(final Response response) {
        final List<Header> headers = response.getHeaders();
        if (headers == null) {
            return null;
        }

        for (final Header header : headers) {
            if (header.getName() != null && header.getName().equals("Link")) {
                final String[] parts = header.getValue().split(",");
                return new PaginationLink(parts[0]);
            }
        }
        return null;
    }

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("Accept", getAcceptHeader());
        request.addHeader("Authorization", "token " + mStoreCredentials.token());
    }

    public String getAcceptHeader() {
        return "application/vnd.github.v3.full+json";
    }

    @Override
    public void log(String message) {
        Log.v("RETROFIT_LOG", message);
    }
}
