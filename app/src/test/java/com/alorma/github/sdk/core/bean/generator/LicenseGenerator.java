package com.alorma.github.sdk.core.bean.generator;

import com.alorma.github.sdk.core.repositories.License;
import com.sergiandreplace.testabean.annotation.TargetClass;
import com.sergiandreplace.testabean.generator.Generator;

@TargetClass({ License.class }) public class LicenseGenerator implements Generator<License> {
  @Override
  public License next() {
    return new License();
  }
}
