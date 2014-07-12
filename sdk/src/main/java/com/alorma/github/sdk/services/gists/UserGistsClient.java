package com.alorma.github.sdk.services.gists;

import android.content.Context;

import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.client.BaseClient;
import com.alorma.github.sdk.services.gists.GistsService;

import retrofit.RestAdapter;

/**
 * Created by Bernat on 08/07/2014.
 */
public class UserGistsClient extends BaseClient<ListGists> {



    public UserGistsClient(Context context) {
        super(context);
    }

    @Override
    protected void executeService(RestAdapter restAdapter) {
        GistsService gistsService = restAdapter.create(GistsService.class);
        gistsService.userGistsList(this);
    }
}
