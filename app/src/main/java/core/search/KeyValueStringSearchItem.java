package core.search;

public class KeyValueStringSearchItem extends SearchItem<String> {
  public KeyValueStringSearchItem(String key, String value) {
    super(key, value);
  }

  @Override
  protected String getPrintableValue(String value) {
    return value;
  }
}
