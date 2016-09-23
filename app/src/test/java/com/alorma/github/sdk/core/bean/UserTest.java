package com.alorma.github.sdk.core.bean;

import com.alorma.github.sdk.core.User;
import com.sergiandreplace.testabean.TestABean;
import com.sergiandreplace.testabean.exception.FieldException;
import org.junit.Test;

public class UserTest {

  @Test
  public void shouldTestUserBean() throws FieldException {
    new TestABean(User.class).test();
  }

}
