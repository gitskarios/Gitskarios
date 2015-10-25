package com.alorma.github.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alorma.github.sdk.services.emojis.EmojisClient;
import com.alorma.gitskarios.core.client.BaseClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisProvider implements BaseClient.OnResultCallback<HashMap<String, String>> {

    public EmojisProvider(Context context) {
        this.context = context;
    }

    private Context context;
    private EmojisCallback emojisCallback;


    public List<Emoji> getEmojis() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String emojis = preferences.getString("EMOJIS", null);
        if (emojis == null) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(emojis, ListEmojis.class);
        }
    }

    public List<Emoji> getEmojis(String filter) {
        List<Emoji> emojis = getEmojis();
        if (filter != null) {
            List<Emoji> emojisFilter = new ArrayList<>();
            for (Emoji emoji : emojis) {
                if (emoji.getKey().contains(filter)) {
                    emojisFilter.add(emoji);
                }
            }
            return emojisFilter;
        } else {
            return emojis;
        }
    }

    public void getEmojis(EmojisCallback emojisCallback) {

        this.emojisCallback = emojisCallback;

        List<Emoji> result = getEmojis();

        if (result.size() > 0) {
            if (emojisCallback != null) {
                emojisCallback.onEmojisLoaded(result);
            }
        } else {
            EmojisClient emojisClient = new EmojisClient(context);
            emojisClient.setOnResultCallback(this);
            emojisClient.execute();
        }
    }

    public void getEmojis(EmojisCallback callback, String filter) {
        if (filter != null) {
            callback.onEmojisLoaded(getEmojis(filter));
        } else {
            getEmojis(callback);
        }
    }

    @Override
    public void onResponseOk(HashMap<String, String> emojis, Response r) {
        if (emojis.size() > 0) {
            List<Emoji> emojisList = new ArrayList<>();

            for (String key : emojis.keySet()) {
                Emoji emoji = new Emoji(key, emojis.get(key));
                emojisList.add(emoji);
                Gson gson = new Gson();
                String json = gson.toJson(emojisList);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().putString("EMOJIS", json).apply();
            }

            if (emojisCallback != null) {
                emojisCallback.onEmojisLoaded(emojisList);
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        if (emojisCallback != null) {
            emojisCallback.onEmojisLoadFail();
        }
    }

    public interface EmojisCallback {
        void onEmojisLoaded(List<Emoji> emojis);

        void onEmojisLoadFail();
    }
}
