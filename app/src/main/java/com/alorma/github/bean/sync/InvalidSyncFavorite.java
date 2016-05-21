package com.alorma.github.bean.sync;

import java.util.HashMap;
import java.util.Map;

public class InvalidSyncFavorite extends SyncFavorite {

  public InvalidSyncFavorite(Map<String, Object> map) {
    super(map);
  }

  public InvalidSyncFavorite() {
    super(Type.INVALID);
  }

  @Override
  public Map<String, Object> toMap() {
    return new HashMap<>();
  }
}
