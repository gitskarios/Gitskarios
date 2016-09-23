package com.alorma.github.sdk.bean.dto.response;

import core.User;
import core.repositories.releases.Asset;

public class ReleaseAsset {

  public String url;
  public String browser_download_url;
  public int id;
  public String name;
  public String labnel;
  public String state;
  public String content_type;
  public long size = 0;
  public int download_count;
  public String created_at;
  public String updated_at;
  public User uploader;

  public ReleaseAsset() {
  }

  public ReleaseAsset(Asset asset) {
    this.url = asset.getUrl();
    this.browser_download_url = asset.getBrowserDownloadUrl();
    this.id = asset.getId();
    this.name = asset.getName();
    this.labnel = asset.getLabel();
    this.state = asset.getState();
    this.content_type = asset.getContentType();
    this.size = asset.getSize();
    this.download_count = asset.getDownloadCount();
    this.created_at = asset.getCreatedAt().toString();
    this.updated_at = asset.getUpdatedAt().toString();
    this.uploader = asset.getUser();
  }
}
