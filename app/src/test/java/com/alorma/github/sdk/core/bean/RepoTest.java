package com.alorma.github.sdk.core.bean;

import com.alorma.github.sdk.core.bean.generator.DateGenerator;
import com.alorma.github.sdk.core.bean.generator.LicenseGenerator;
import com.alorma.github.sdk.core.bean.generator.PermissionsGenerator;
import com.alorma.github.sdk.core.bean.generator.RepoGenerator;
import com.alorma.github.sdk.core.bean.generator.UserGenerator;
import com.alorma.github.sdk.core.repositories.Repo;
import com.sergiandreplace.testabean.Configuration;
import com.sergiandreplace.testabean.TestABean;
import com.sergiandreplace.testabean.exception.FieldException;
import com.sergiandreplace.testabean.generator.GeneratorFactory;
import org.junit.Test;

public class RepoTest {

  @Test
  public void shouldTestRepoBean() throws FieldException {
    GeneratorFactory factory = new GeneratorFactory();
    factory.add(new UserGenerator());
    factory.add(new DateGenerator());
    factory.add(new RepoGenerator());
    factory.add(new LicenseGenerator());
    factory.add(new PermissionsGenerator());
    Configuration config = new Configuration.Builder().setGeneratorFactory(factory).build();
    new TestABean(Repo.class, config).test();
  }
}
