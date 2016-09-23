package com.alorma.github.sdk.core.bean.generator;

import com.alorma.github.sdk.core.User;
import com.alorma.github.sdk.core.repositories.Repo;
import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;
import java.util.Date;

@TargetClass({Repo.class})
public class RepoGenerator implements Generator<Repo> {
  @Override
  public Repo next() {
    Repo repo = new Repo();
    repo.setId(1);
    repo.setName("aaa");
    repo.setFullName("AA a A");
    repo.setOwner(new User());
    return repo;
  }
}
