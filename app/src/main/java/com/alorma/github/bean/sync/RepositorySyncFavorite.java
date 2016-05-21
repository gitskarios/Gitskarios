package com.alorma.github.bean.sync;

import java.util.HashMap;
import java.util.Map;

public class RepositorySyncFavorite extends SyncFavorite {
  private String name;
  private String owner;

  public RepositorySyncFavorite() {
    super(Type.REPOSITORY);
  }

  public RepositorySyncFavorite(Map<String, Object> map) {
    super(map);
    if (map != null) {
      if (map.containsKey("name") && map.get("name") != null) {
        this.name = (String) map.get("name");
      }
      if (map.containsKey("owner") && map.get("owner") != null) {
        this.owner = (String) map.get("owner");
      }
    }
  }

  public RepositorySyncFavorite(String name, String owner) {
    super(Type.REPOSITORY);
    this.name = name;
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return owner;
  }

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    map.put("owner", owner);
    return map;
  }
}
