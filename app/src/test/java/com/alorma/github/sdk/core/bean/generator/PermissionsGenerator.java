package com.alorma.github.sdk.core.bean.generator;

import com.alorma.github.sdk.core.repositories.Permissions;
import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;
import java.util.Date;

@TargetClass({Permissions.class})
public class PermissionsGenerator implements Generator<Permissions> {
  @Override
  public Permissions next() {
    return new Permissions();
  }
}
