package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcelable;
import core.User;
import java.util.Date;

public class GistRevision extends ShaUrl implements Parcelable {
  public Date committedAt;
  public GitChangeStatus changeStatus;
  public String version;
  public User user;

  public GistRevision() {
  }
}