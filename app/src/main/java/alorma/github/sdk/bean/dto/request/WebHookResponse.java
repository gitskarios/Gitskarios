package com.alorma.github.sdk.bean.dto.request;

public class WebHookResponse {
  public long id;
  public String name;
  public String[] events;
  public String active;
  public WebHookConfigRequest config;
}
