package com.alorma.github.sdk.bean;

import com.alorma.github.sdk.bean.info.RepoInfo;

public class ReadmeInfo {
  private RepoInfo repoInfo;
  private boolean truncate;

  public RepoInfo getRepoInfo() {
    return repoInfo;
  }

  public void setRepoInfo(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
  }

  public boolean isTruncate() {
    return truncate;
  }

  public void setTruncate(boolean truncate) {
    this.truncate = truncate;
  }

  @Override
  public String toString() {
    return "ReadmeInfo{" +
        "repoInfo=" + repoInfo +
        ", truncate=" + truncate +
        '}';
  }
}
