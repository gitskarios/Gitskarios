package core.search;

public abstract class SearchItem<T> {
  private final String key;
  private final T value;

  public SearchItem(String key, T value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    return key + ":" + getPrintableValue(value);
  }

  protected abstract String getPrintableValue(T value);
}
