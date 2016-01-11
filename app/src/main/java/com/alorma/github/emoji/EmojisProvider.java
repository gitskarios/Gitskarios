package com.alorma.github.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alorma.github.sdk.services.emojis.EmojisClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisProvider {

    private Context context;

    public EmojisProvider(Context context) {
        this.context = context;
    }

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

    public void getEmojis(final EmojisCallback emojisCallback) {

        List<Emoji> result = getEmojis();

        if (result.size() > 0) {
            if (emojisCallback != null) {
                emojisCallback.onEmojisLoaded(result);
            }
        } else {
            EmojisClient emojisClient = new EmojisClient();
            emojisClient.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<HashMap<String, String>>() {

                List<Emoji> emojisList = new ArrayList<>();

                @Override
                public void onCompleted() {
                    if (emojisCallback != null) {
                        emojisCallback.onEmojisLoaded(emojisList);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (emojisCallback != null) {
                        emojisCallback.onEmojisLoadFail();
                    }
                }

                @Override
                public void onNext(HashMap<String, String> emojis) {
                    if (emojis.size() > 0) {

                        for (String key : emojis.keySet()) {
                            Emoji emoji = new Emoji(key, emojis.get(key));
                            emojisList.add(emoji);
                            Gson gson = new Gson();
                            String json = gson.toJson(emojisList);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            preferences.edit().putString("EMOJIS", json).apply();
                        }
                    }
                }
            });
        }
    }

    public void getEmojis(EmojisCallback callback, String filter) {
        if (filter != null) {
            callback.onEmojisLoaded(getEmojis(filter));
        } else {
            getEmojis(callback);
        }
    }

    public interface EmojisCallback {
        void onEmojisLoaded(List<Emoji> emojis);

        void onEmojisLoadFail();
    }
}
