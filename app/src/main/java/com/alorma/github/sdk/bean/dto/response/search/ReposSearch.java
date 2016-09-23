package com.alorma.github.sdk.bean.dto.response.search;

import android.os.Parcelable;
import core.repositories.Repo;
import java.util.List;

public class ReposSearch extends SearchBase implements Parcelable {
  public List<Repo> items;

  public ReposSearch() {
  }
}
