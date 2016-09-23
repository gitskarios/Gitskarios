package com.alorma.github.sdk.bean.dto.response;

import core.User;
import core.repositories.releases.Asset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Release {

  public String body;
  public String upload_url;
  public String assets_url;
  public String tag_name;
  public String url;
  public String published_at;
  public String html_url;
  public String id;
  public String target_commitish;
  public List<ReleaseAsset> assets;
  public boolean draft;
  public User author;
  public String zipball_url;
  public boolean prerelease;
  public String tarball_url;
  public String name;
  public Date created_at;

  public Release() {

  }

  private List<ReleaseAsset> createAssets(List<Asset> assets) {
    List<ReleaseAsset> result = new ArrayList<>(assets.size());
    for (Asset asset : assets) {
      ReleaseAsset releaseAsset = new ReleaseAsset(asset);
      result.add(releaseAsset);
    }
    return result;
  }
}
