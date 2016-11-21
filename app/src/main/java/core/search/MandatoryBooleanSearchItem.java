package core.search;

import java.security.InvalidParameterException;

public class MandatoryBooleanSearchItem extends SearchItem<Boolean> {
  private final String textOn;
  private final String textOff;

  public MandatoryBooleanSearchItem(String key, Boolean value, String textOn, String textOff) {
    super(key, value);
    this.textOn = textOn;
    this.textOff = textOff;
  }

  @Override
  protected String getPrintableValue(Boolean value) {
    if (value != null) {
      return value ? textOn : textOff;
    } else {
      throw new InvalidParameterException("Value is mandatory");
    }
  }
}
