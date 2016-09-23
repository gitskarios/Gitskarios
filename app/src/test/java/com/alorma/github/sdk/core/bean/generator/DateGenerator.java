package com.alorma.github.sdk.core.bean.generator;

import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;
import java.util.Date;

@TargetClass({Date.class})
public class DateGenerator implements Generator<Date> {
  @Override
  public Date next() {
    return new Date();
  }
}
