package com.alorma.yaml.formbuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) public class YamlFormItem {
  @JsonProperty("hint") public String hint;
  @JsonProperty("checked") public boolean checked;
  @JsonProperty("max") public int max;
  @JsonProperty("multiline") public boolean multiline;
  @JsonProperty("values") public List<String> values;
  @JsonProperty("mode") public YamlFormObjectMode mode;
  @JsonProperty("extras") public YamlFormItemExtras extras;

  @Override
  public String toString() {
    return "YamlFormItem{" +
        "hint='" + hint + '\'' +
        ", max=" + max +
        ", multiline=" + multiline +
        ", values=" + values +
        ", mode=" + mode +
        ", extras=" + extras +
        '}';
  }
}
