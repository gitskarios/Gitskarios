package com.alorma.github.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.google.gson.Gson;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public class EmojisCacheDataSource implements CacheDataSource<Void, List<Emoji>> {
  private static final String EMOJIS = "EMOJIS";
  private Context context;

  public EmojisCacheDataSource(Context context) {
    this.context = context;
  }

  @Override
  public void saveData(SdkItem<Void> request, SdkItem<List<Emoji>> data) {

  }

  @Override
  public Observable<SdkItem<List<Emoji>>> getData(SdkItem<Void> request) {
    return Observable.defer(() -> {
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      return Observable.just(preferences.getString(EMOJIS, null));
    })
        .filter(s -> s != null)
        .doOnNext(this::save)
        .map((Func1<String, List<Emoji>>) text -> new Gson().fromJson(text, ListEmojis.class))
        .map(SdkItem::new);
  }

  private void save(String s) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    preferences.edit().putString(EMOJIS, s).apply();
  }
}
