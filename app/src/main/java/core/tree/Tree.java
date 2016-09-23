package core.tree;

import java.util.List;

public class Tree {
  private String sha;
  private String url;
  private boolean truncated;
  private List<TreeContent> tree;

  public String getSha() {
    return sha;
  }

  public String getUrl() {
    return url;
  }

  public boolean isTruncated() {
    return truncated;
  }

  public List<TreeContent> getTree() {
    return tree;
  }
}
