package com.alorma.github.sdk.security;

import java.util.List;

/**
 * Created by Bernat on 08/07/2014.
 */
public class OAuthRequestDTO {
  private List<String> scopes;
  private String note;
  private String client_id;
  private String client_secret;

  public List<String> getScopes() {
    return scopes;
  }

  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getClient_id() {
    return client_id;
  }

  public void setClient_id(String client_id) {
    this.client_id = client_id;
  }

  public String getClient_secret() {
    return client_secret;
  }

  public void setClient_secret(String client_secret) {
    this.client_secret = client_secret;
  }
}
