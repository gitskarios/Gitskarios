package com.alorma.github.ui;

public class GithubSmsReader {
  public String getCode(String text) {
    String code = text.split(" ")[0];
    if (code.matches("[0-9]+") && code.length() > 2) {
      return code;
    }
    return "";
  }
}
