package com.alorma.github.presenter.repos;

import com.alorma.github.sdk.core.repositories.Repo;
import java.util.List;

public class UserReposCache
    implements com.alorma.github.sdk.core.datasource.CacheDataSource<String,List<Repo>> {
  @Override
  public void saveData(String s, List<Repo> repos) {

  }

  @Override
  public List<Repo> getData(String s) {
    return null;
  }
}
