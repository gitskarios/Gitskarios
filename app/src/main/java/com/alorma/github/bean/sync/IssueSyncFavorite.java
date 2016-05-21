package com.alorma.github.bean.sync;

import java.util.HashMap;
import java.util.Map;

public class IssueSyncFavorite extends RepositorySyncFavorite {

  private long number;

  public IssueSyncFavorite() {
    super();
    setType(Type.ISSUE);
  }

  public IssueSyncFavorite(Map<String, Object> map) {
    super(map);
    if (map != null) {
      if (map.containsKey("number") && map.get("number") != null) {
        this.number = (long) map.get("number");
      }
    }
  }

  public IssueSyncFavorite(String name, String owner, long number) {
    super(name, owner);
    setType(Type.ISSUE);
    this.number = number;
  }

  public long getNumber() {
    return number;
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> map = super.toMap();
    if (map == null) {
      map = new HashMap<>();
      map.put("name", getName());
      map.put("owner", getOwner());
    }
    map.put("number", number);
    return map;
  }
}
