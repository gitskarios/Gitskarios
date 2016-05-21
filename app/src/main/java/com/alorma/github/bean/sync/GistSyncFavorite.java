package com.alorma.github.bean.sync;

import java.util.HashMap;
import java.util.Map;

public class GistSyncFavorite extends SyncFavorite {
  private String owner;
  private String id;

  public GistSyncFavorite() {
    super(Type.GIST);
  }

  public GistSyncFavorite(String owner, String id) {
    super(Type.GIST);
    this.owner = owner;
    this.id = id;
  }

  public GistSyncFavorite(Map<String, Object> map) {
    super(map);
    if (map != null) {
      if (map.containsKey("id") && map.get("id") != null) {
        this.id = (String) map.get("id");
      }
      if (map.containsKey("owner") && map.get("owner") != null) {
        this.owner = (String) map.get("owner");
      }
    }
  }

  public String getOwner() {
    return owner;
  }

  public String getId() {
    return id;
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("owner", owner);
    map.put("id", id);
    return map;
  }
}
