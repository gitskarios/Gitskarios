package com.alorma.github.sdk.bean.dto.response;

import com.alorma.github.sdk.bean.dto.response.events.EventType;
import com.alorma.github.sdk.bean.dto.response.events.payload.Payload;
import com.google.gson.annotations.SerializedName;
import core.User;
import core.repositories.Repo;

public class GithubEvent {

  public long id;
  public EventType type = EventType.Unhandled;
  public String name;
  public User actor;
  public User org;
  public Repo repo;
  public Payload payload;
  @SerializedName("public") public boolean public_event;
  public String created_at;

  public GithubEvent() {
  }

  public EventType getType() {
    return type != null ? type : EventType.Unhandled;
  }

}
