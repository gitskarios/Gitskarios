package com.alorma.github.sdk.bean.dto.response;

import core.User;
import core.repositories.Repo;

public class Head extends ShaUrl {
  public String ref;
  public Repo repo;
  public String label;
  public User user;

  public Head() {
  }
}
