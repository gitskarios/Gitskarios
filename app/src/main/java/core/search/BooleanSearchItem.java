package core.search;

public class BooleanSearchItem extends SearchItem<Boolean> {
  private final String textOn;
  private final String textOff;

  public BooleanSearchItem(String key, Boolean value, String textOn, String textOff) {
    super(key, value);
    this.textOn = textOn;
    this.textOff = textOff;
  }

  @Override
  protected String getPrintableValue(Boolean value) {
    return (value != null && value) ? textOn : textOff;
  }
}
