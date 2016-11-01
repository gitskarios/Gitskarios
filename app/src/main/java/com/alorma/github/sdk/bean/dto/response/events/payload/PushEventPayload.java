package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcelable;
import core.repositories.Commit;
import java.util.List;

public class PushEventPayload extends GithubEventPayload implements Parcelable {
  public long push_id;
  public int size;
  public int distinct_size;
  public String ref;
  public String head;
  public String before;
  public List<Commit> commits;

  public PushEventPayload() {
  }

  public long getPush_id() {
    return push_id;
  }

  public void setPush_id(long push_id) {
    this.push_id = push_id;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getDistinct_size() {
    return distinct_size;
  }

  public void setDistinct_size(int distinct_size) {
    this.distinct_size = distinct_size;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public String getHead() {
    return head;
  }

  public void setHead(String head) {
    this.head = head;
  }

  public String getBefore() {
    return before;
  }

  public void setBefore(String before) {
    this.before = before;
  }

  public List<Commit> getCommits() {
    return commits;
  }

  public void setCommits(List<Commit> commits) {
    this.commits = commits;
  }
}
