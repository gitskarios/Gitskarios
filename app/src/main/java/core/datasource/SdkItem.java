package core.datasource;

public class SdkItem<K> {
  private Integer page;
  private K k;

  public SdkItem(K k) {
    this.page = null;
    this.k = k;
  }

  public SdkItem(Integer page, K k) {
    this.page = page;
    this.k = k;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public K getK() {
    return k;
  }

  public void setK(K k) {
    this.k = k;
  }
}
