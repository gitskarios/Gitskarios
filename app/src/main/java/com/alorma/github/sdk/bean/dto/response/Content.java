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

  public ContentType getType() {
    return type;
  }

  public void setType(ContentType type) {
    this.type = type;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getGit_url() {
    return git_url;
  }

  public void setGit_url(String git_url) {
    this.git_url = git_url;
  }

  public Links get_links() {
    return _links;
  }

  public void set_links(Links _links) {
    this._links = _links;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public List<Content> getChildren() {
    return children;
  }

  public void setChildren(List<Content> children) {
    this.children = children;
  }

  public Content getParent() {
    return parent;
  }

  public void setParent(Content parent) {
    this.parent = parent;
  }
}
