package com.alorma.github.utils;

import java.util.UUID;

public class AccountUtils {
  public String getNameForAccount(String name) {
    return name + "-" + UUID.randomUUID().toString();
  }

  public String getNameFromAccount(String name) {
    return name.split("-")[0];
  }
}
