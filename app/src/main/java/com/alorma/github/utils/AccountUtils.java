package com.alorma.github.utils;

public class AccountUtils {
  public String getNameForAccount(String name) {
    return name;
  }

  public String getNameFromAccount(String name) {
    name = name.replaceAll("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "");
    return method(name);
  }

  public String method(String str) {
    if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '-') {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }
}
