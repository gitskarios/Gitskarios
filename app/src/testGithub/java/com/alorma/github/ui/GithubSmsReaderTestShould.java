package com.alorma.github.ui;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubSmsReaderTestShould {

  public static final String CODE = "628248";
  public static final String CODE_TEXT = "628248 is your GitHub authentication code.";
  public static final String CODE_TEXT_INVALID = "is your GitHub authentication code.";

  GithubSmsReader smsReader;

  @Before
  public void setUp() {
    smsReader = new GithubSmsReader();
  }

  @Test(expected = NullPointerException.class)
  public void throwNullPointerException_whenNullText() {
    smsReader.getCode(null);
  }

  @Test
  public void returnEmptyCode_whenEmptyText() {
    String code = smsReader.getCode("");

    assertThat(code).isEmpty();
  }

  @Test
  public void returnEmptyCode_whenTextNotContainsCode() {
    String code = smsReader.getCode(CODE_TEXT_INVALID);

    assertThat(code).isEmpty();
  }

  @Test
  public void returnCode_whenTextContainsCode() {
    String code = smsReader.getCode(CODE_TEXT);

    assertThat(code).isNotEmpty().isEqualToIgnoringCase(CODE);
  }
}