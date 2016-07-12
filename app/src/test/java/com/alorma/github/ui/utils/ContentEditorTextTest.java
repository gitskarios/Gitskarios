package com.alorma.github.ui.utils;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContentEditorTextTest {

  public static final String NAME = "AAaAA";
  public static final String LINK = "http://i.imgur.com/5iFkXQl.jpg";
  public static final String RESULT = "![](http://i.imgur.com/5iFkXQl.jpg)";
  public static final String RESULT_WITH_NAME = "![" + NAME + "](" + LINK + ")";

  private ContentEditorText contentText;

  @Before
  public void setUp() {
    contentText = new ContentEditorText();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenLinkIsNull() throws Exception {
    contentText.getTextForImage(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenLinkIsEmpty() throws Exception {
    contentText.getTextForImage("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenLinkNotStartWithHttp() throws Exception {
    contentText.getTextForImage("i.imgur.com/5iFkXQl.jpg");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenNameNullAndLinkIsNull() throws Exception {
    contentText.getTextForImage(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenNameNullAndLinkIsEmpty() throws Exception {
    contentText.getTextForImage(null, "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenNameEmptyAndLinkIsNull() throws Exception {
    contentText.getTextForImage("", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowException_whenNameEmptyAndLinkIsEmpty() throws Exception {
    contentText.getTextForImage("", "");
  }

  @Test
  public void shouldBuildText_whenLinkIsOk() {
    String text = contentText.getTextForImage(LINK);
    assertThat(text).isNotEmpty().isEqualToIgnoringCase(RESULT);
  }

  @Test
  public void shouldBuildText_whenNameIsOkAndLinkIsOk() {
    String text = contentText.getTextForImage(NAME, LINK);
    assertThat(text).isNotEmpty().isEqualToIgnoringCase(RESULT_WITH_NAME);
  }
}