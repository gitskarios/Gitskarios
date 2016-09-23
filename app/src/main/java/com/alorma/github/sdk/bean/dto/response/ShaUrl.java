package com.alorma.github.sdk.bean.dto.response;

public class ShaUrl {

  private static final int MAX_SHA_LENGHT = 8;
  public String sha;
  public String url;
  public String html_url;

  public ShaUrl() {
  }

  public static String shortShaStatic(String sha) {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }

  public String shortSha() {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }
}
