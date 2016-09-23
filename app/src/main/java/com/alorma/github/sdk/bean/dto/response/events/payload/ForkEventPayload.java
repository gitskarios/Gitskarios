package com.alorma.github.sdk.bean.dto.response.events.payload;

import core.repositories.Repo;

public class ForkEventPayload extends GithubEventPayload {
  public Repo forkee;
  public Repo repository;

  public ForkEventPayload() {
  }
}
