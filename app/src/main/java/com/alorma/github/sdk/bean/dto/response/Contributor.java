package com.alorma.github.sdk.bean.dto.response;

import core.User;
import java.util.List;

public class Contributor {
  public User author;
  public int id;
  public int total;
  public List<Week> weeks;

  public Contributor() {
  }
}
