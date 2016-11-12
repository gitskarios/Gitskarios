package com.alorma.github.presenter;

import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;
import java.util.concurrent.TimeUnit;

public class CacheWrapper {

  private static QNCache qnCacheRepos;

  public static QNCache cache() {
    if (qnCacheRepos == null) {
      qnCacheRepos = new QNCacheBuilder().setDefaultKeepaliveInMillis(TimeUnit.SECONDS.toMillis(30)).createQNCache();
    }
    return qnCacheRepos;
  }
}
