package com.alorma.github.emoji;

import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.sdk.core.datasource.CacheDataSource;
import com.alorma.github.sdk.core.datasource.SdkItem;
import java.util.List;
import rx.Observable;

public class EmojisCacheDataSource implements CacheDataSource<Void, List<Emoji>> {

  public EmojisCacheDataSource() {
  }

  @Override
  public void saveData(SdkItem<Void> request, SdkItem<List<Emoji>> data) {
    CacheWrapper.setEmojis(data.getK());
  }

  @Override
  public Observable<SdkItem<List<Emoji>>> getData(SdkItem<Void> request) {
    return Observable.defer(() -> Observable.just(CacheWrapper.getEmojis())).filter(emojis -> emojis != null).map(SdkItem::new);
  }
}
