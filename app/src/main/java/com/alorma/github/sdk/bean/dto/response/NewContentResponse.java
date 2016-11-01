package com.alorma.github.sdk.bean.dto.response;

import core.repositories.Commit;

public class NewContentResponse {
  public Content content;
  public Commit commit;

  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }

  public Commit getCommit() {
    return commit;
  }

  public void setCommit(Commit commit) {
    this.commit = commit;
  }
}
