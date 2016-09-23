package com.alorma.github.sdk.bean.dto.response.events.payload;

import com.alorma.github.sdk.bean.dto.response.Release;
import core.User;
import core.repositories.Repo;

public class ReleaseEventPayload extends GithubEventPayload {
  public String action;
  public Release release;
  public Repo repository;
  public User sender;

  public ReleaseEventPayload() {
  }
}
