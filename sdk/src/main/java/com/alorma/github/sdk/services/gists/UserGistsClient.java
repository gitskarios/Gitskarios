package com.alorma.github.sdk.services.gists;

import android.app.Activity;
import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.services.client.BaseClient;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 08/07/2014.
 */
public class UserGistsClient extends BaseClient<ListGists> {

    private String username;
    private int page = 0;

    public UserGistsClient(Context context) {
        super(context);
    }
    public UserGistsClient(Context context, String username) {
        super(context);
        this.username = username;
    }

    public UserGistsClient(Context context, String username, int page) {
        super(context);
        this.username = username;
        this.page = page;
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        GistsService gistsService = restAdapter.create(GistsService.class);
        if (page == 0) {
            if (username == null) {
                gistsService.userGistsList(this);
            } else {
                gistsService.userGistsList(username, this);
            }
        } else {
            if (username == null) {
                gistsService.userGistsList(page, this);
            } else {
                gistsService.userGistsList(username, page, this);
            }
        }
    }
}
