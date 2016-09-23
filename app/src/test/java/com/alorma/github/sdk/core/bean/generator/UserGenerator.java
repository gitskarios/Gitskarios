package com.alorma.github.sdk.core.bean.generator;

import com.alorma.github.sdk.core.User;
import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;

@TargetClass({User.class})
public class UserGenerator implements Generator<User> {
  @Override
  public User next() {
    User user = new User();
    user.setId(1);
    user.setLogin("aaa");
    user.setName("AA a A");
    return user;
  }
}
