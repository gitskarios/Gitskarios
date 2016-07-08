package com.alorma.github.sdk.bean.dto.request;

public class WebHookRequest {
  public String name;
  public boolean active;
  public String[] events;
  public WebHookConfigRequest config;
}
