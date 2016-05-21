package com.alorma.github.bean.sync;

import java.util.Map;

public abstract class SyncFavorite {

  public enum Type {
    REPOSITORY,
    ISSUE,
    GIST,
    PULL_REQUEST,
    INVALID
  }

  private Type type;

  public SyncFavorite() {

  }

  public SyncFavorite(Map<String, Object> map) {
    if (map != null && map.containsKey("type") && map.get("type") != null) {
      String type = (String) map.get("type");
      if (Type.REPOSITORY.name().equals(type)) {
        this.type = Type.REPOSITORY;
      } else if (Type.ISSUE.name().equals(type)) {
        this.type = Type.ISSUE;
      } else if (Type.GIST.name().equals(type)) {
        this.type = Type.GIST;
      }
    }
  }

  public SyncFavorite(Type type) {
    this.type = type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public abstract Map<String, Object> toMap();
}
