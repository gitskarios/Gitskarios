package com.alorma.github.cache;

import com.fewlaps.quitnowcache.QNCache;
import com.fewlaps.quitnowcache.QNCacheBuilder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bernat on 17/10/2015.
 */
public class QnCacheProvider {

  private static final Map<TYPE, QNCache> mapCaches = new HashMap<>();

  public static QNCache getInstance(TYPE type) {
    if (mapCaches.get(type) == null) {
      QNCache qnCache = new QNCacheBuilder().createQNCache();
      mapCaches.put(type, qnCache);
    }
    return mapCaches.get(type);
  }

  public enum TYPE {
    REPO,
    ISSUE,
    PULL_REQUEST,
    PROFILE
  }
}
