package com.alorma.github.ui.utils;

public class ContentEditorText {

  private static final String IMG_SIGN = "!";
  private static final String IMG_NAME_OPEN = "[";
  private static final String IMG_NAME_CLOSE = "]";
  private static final String IMG_IMAGE_OPEN = "(";
  private static final String IMG_IMAGE_CLOSE = ")";

  public String getTextForImage(String link) {
    return getTextForImage(null, link);
  }

  public String getTextForImage(String name, String link) {
    if (link == null) {
      throw new IllegalArgumentException("Link cannot be null");
    }
    if (link.isEmpty()) {
      throw new IllegalArgumentException("Link cannot be empty");
    }
    if (!link.startsWith("http")) {
      throw new IllegalArgumentException("Link should start with http");
    }
    StringBuilder builder = new StringBuilder();
    builder.append(IMG_SIGN);
    builder.append(IMG_NAME_OPEN);
    if (name != null) {
      builder.append(name);
    }
    builder.append(IMG_NAME_CLOSE);
    builder.append(IMG_IMAGE_OPEN);
    builder.append(link);
    builder.append(IMG_IMAGE_CLOSE);
    return builder.toString();
  }
}
