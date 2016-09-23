package com.alorma.github.sdk.core.bean;

import com.alorma.github.sdk.core.bean.generator.DateGenerator;
import com.alorma.github.sdk.core.bean.generator.LicenseGenerator;
import com.alorma.github.sdk.core.bean.generator.NotificationSubjectGenerator;
import com.alorma.github.sdk.core.bean.generator.PermissionsGenerator;
import com.alorma.github.sdk.core.bean.generator.RepoGenerator;
import com.alorma.github.sdk.core.notifications.Notification;
import com.sergiandreplace.testabean.Configuration;
import com.sergiandreplace.testabean.TestABean;
import com.sergiandreplace.testabean.exception.FieldException;
import com.sergiandreplace.testabean.generator.GeneratorFactory;
import org.junit.Test;

public class NotificationTest {

  @Test
  public void shouldTestNotificationBean() throws FieldException {
    GeneratorFactory factory = new GeneratorFactory();
    factory.add(new RepoGenerator());
    factory.add(new DateGenerator());
    factory.add(new PermissionsGenerator());
    factory.add(new LicenseGenerator());
    factory.add(new NotificationSubjectGenerator());
    Configuration config = new Configuration.Builder().setGeneratorFactory(factory).build();
    new TestABean(Notification.class, config).test();
  }
}
