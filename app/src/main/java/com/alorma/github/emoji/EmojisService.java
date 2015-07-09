package com.alorma.github.emoji;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Bernat on 08/07/2015.
 */
public interface EmojisService {

    @GET("/emojis")
    void getEmojis(Callback<HashMap<String, String>> callback);

}
