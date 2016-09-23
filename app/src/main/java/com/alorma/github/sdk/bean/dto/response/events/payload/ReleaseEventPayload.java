package com.alorma.github.sdk.bean.dto.response.events.payload;

import core.User;
import core.repositories.Repo;
import core.repositories.releases.Release;

public class ReleaseEventPayload extends GithubEventPayload {
  public String action;
  public Release release;
  public Repo repository;
  public User sender;

  public ReleaseEventPayload() {
  }
}
