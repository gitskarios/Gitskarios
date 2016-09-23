package core.tree;

import java.util.List;

public class TreeContent {
  private String path;
  private String mode;
  private String type;
  private String sha;
  private String url;
  private int size;
  private List<TreeContent> tree;

  public String getPath() {
    return path;
  }

  public String getMode() {
    return mode;
  }

  public String getType() {
    return type;
  }

  public String getSha() {
    return sha;
  }

  public String getUrl() {
    return url;
  }

  public int getSize() {
    return size;
  }

  public List<TreeContent> getTree() {
    return tree;
  }

  public void setTree(List<TreeContent> tree) {
    this.tree = tree;
  }
}
