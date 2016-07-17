package com.alorma.github.emoji;

import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import com.alorma.github.sdk.core.datasource.SdkItem;
import com.alorma.github.sdk.services.emojis.EmojisClient;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class EmojisApiDataSource extends CloudDataSource<Void,List<Emoji>> {
  public EmojisApiDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected Observable<SdkItem<List<Emoji>>> execute(SdkItem<Void> request, RestWrapper service) {
    return getApiEmojis().map(SdkItem::new);
  }

  private Observable<List<Emoji>> getApiEmojis() {
    EmojisClient emojisClient = new EmojisClient();
    return emojisClient.observable().map(items -> {
      List<Emoji> emojis = new ArrayList<>(items.size());
      for (String key : items.keySet()) {
        emojis.add(new Emoji(key, items.get(key)));
      }
      return emojis;
    });
  }
}
