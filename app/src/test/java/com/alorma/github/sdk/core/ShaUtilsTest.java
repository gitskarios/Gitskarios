package com.alorma.github.sdk.core;

import core.ShaUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ShaUtilsTest {

  private static final String EMPTY = "";
  private static final String SHA = "8ecc254067e0f3dd7ba1410352eac4e55e886801";
  private static final String SHORT_SHA = "8ecc254";
  private static final String SHORTEST_SHA = "8e";

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrow_whenNull() {
    try {
      ShaUtils.shortSha(null);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrow_whenEmpty() {
    try {
      ShaUtils.shortSha(EMPTY);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void shouldThrow_whenTooMuchShort() {
    try {
      ShaUtils.shortSha(SHORTEST_SHA);
    } catch (IllegalArgumentException e) {
      throw e;
    }
  }
  @Test
  public void shouldReturnValidSha_whenNonEmpty() {
    assertThat(ShaUtils.shortSha(SHA)).isNotEmpty().isEqualTo(SHORT_SHA);
  }
}