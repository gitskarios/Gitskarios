package com.alorma.github.utils;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountUtilsTestShould {

  private AccountUtils accountUtils;

  @Before
  public void setUp() {
    accountUtils = new AccountUtils();
  }

  @Test
  public void returnNameFromNoUUIDName() {
    String name = "alorma";

    String nameFromAccount = accountUtils.getNameFromAccount(name);

    assertThat(nameFromAccount).isEqualTo(name);
  }

  @Test
  public void returnNameFromNoUUIDNameWithDash() {
    String name = "nv-vm";

    String nameFromAccount = accountUtils.getNameFromAccount(name);

    assertThat(nameFromAccount).isEqualTo(name);
  }

  @Test
  public void returnNameFromUUIDName() {
    String name = "alorma";

    String nameFromAccount = accountUtils.getNameFromAccount(name + "-" + UUID.randomUUID().toString());

    assertThat(nameFromAccount).isEqualTo(name);
  }

  @Test
  public void returnNameFromUUIDNameWithDash() {
    String name = "nv-vm";

    String nameFromAccount = accountUtils.getNameFromAccount(name + "-" + UUID.randomUUID().toString());

    assertThat(nameFromAccount).isEqualTo(name);
  }
}