package com.alorma.github.sdk.services.content;

import android.content.Context;
import android.util.Log;

import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.services.client.BaseClient;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Request;
import retrofit.client.Response;

/**
 * Created by Bernat on 22/07/2014.
 */
public class GetMarkdownClient implements Callback<String>, Client {

    private RequestMarkdownDTO readme;

    private BaseClient.OnResultCallback<String> onResultCallback;

    public GetMarkdownClient(Context context, RequestMarkdownDTO readme) {
        this.readme = readme;
    }

    public void execute() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ApiConstants.API_URL)
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setClient(this)
                .build();

        executeService(restAdapter);
    }

    private void executeService(RestAdapter restAdapter) {
        restAdapter.create(ContentService.class).markdown(readme, this);
    }

    @Override
    public void success(String s, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {

    }

    @Override
    public Response execute(Request request) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(request.getUrl());
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-type", "text/plain");

        Log.i("ALORMA_JSON", new Gson().toJson(readme));

        httppost.setEntity(new StringEntity(readme.text));

        HttpResponse response = httpclient.execute(httppost);

        String inputStreamString = new Scanner(response.getEntity().getContent(),"UTF-8").useDelimiter("\\A").next();

        if (onResultCallback != null) {
            onResultCallback.onResponseOk(inputStreamString, null);
        }

        return null;
    }

    public void setOnResultCallback(BaseClient.OnResultCallback<String> onResultCallback) {
        this.onResultCallback = onResultCallback;
    }
}
