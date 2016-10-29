package com.alorma.github.ui.activity.repo;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

public class RepoItem {
  @IdRes private int id;
  private String avatar;
  @DrawableRes private int icon;
  private String content;
  private boolean expandable;

  public int getIcon() {
    return icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isExpandable() {
    return expandable;
  }

  public void setExpandable(boolean expandable) {
    this.expandable = expandable;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public RepoItem withId(int id) {
    this.id = id;
    return this;
  }

  public RepoItem withIcon(int icon) {
    this.icon = icon;
    return this;
  }

  public RepoItem withContent(String content) {
    this.content = content;
    return this;
  }

  public RepoItem withAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  public RepoItem withExpandable(boolean expandable) {
    this.expandable = expandable;
    return this;
  }
}
