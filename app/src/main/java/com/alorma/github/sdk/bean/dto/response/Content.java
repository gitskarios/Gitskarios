package com.alorma.github.sdk.bean.dto.response;

import java.util.List;

public class Content extends ShaUrl implements Comparable<Content> {
  public ContentType type;
  public int size;
  public String name;
  public String content;
  public String path;
  public String git_url;
  public Links _links;
  public String encoding;
  public List<Content> children;
  public Content parent;

  public Content() {
  }

  public boolean isDir() {
    return ContentType.dir.equals(type);
  }

  public boolean isFile() {
    return ContentType.file.equals(type);
  }

  public boolean isSubmodule() {
    return ContentType.symlink.equals(type);
  }

  @Override
  public int compareTo(Content another) {
    if (isDir()) {
      return another.isDir() ? -name.compareTo(another.name) : 1;
    } else if (another.isDir()) {
      return -1;
    }

    return -name.compareTo(another.name);
  }
}
