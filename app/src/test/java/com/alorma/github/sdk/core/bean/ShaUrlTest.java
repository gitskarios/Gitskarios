package com.alorma.github.sdk.core.bean;

import com.alorma.github.sdk.core.ShaUrl;
import com.alorma.github.sdk.core.User;
import com.sergiandreplace.testabean.TestABean;
import com.sergiandreplace.testabean.exception.FieldException;
import org.junit.Test;

public class ShaUrlTest {

  @Test
  public void shouldTestShaUrlBean() throws FieldException {
    new TestABean(ShaUrl.class).test();
  }

}
