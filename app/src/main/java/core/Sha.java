package core;

public class Sha {
  private static final int MAX_SHA_LENGHT = 8;

  public String sha;
  public String url;

  public Sha() {
  }

  public String getSha() {
    return sha;
  }

  public void setSha(String sha) {
    this.sha = sha;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String shortSha() {
    int start = 0;
    int end = Math.min(MAX_SHA_LENGHT, sha.length());

    return sha.substring(start, end);
  }
}
