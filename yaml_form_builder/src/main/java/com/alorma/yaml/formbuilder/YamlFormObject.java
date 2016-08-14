package com.alorma.yaml.formbuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) public class YamlFormObject {

  @JsonProperty("key") public String key;
  @JsonProperty("type") public YamlFormItemType type;
  @JsonProperty("label") public String label;
  @JsonProperty("params") public YamlFormItem params;

  @Override
  public String toString() {
    return "YamlFormObject{" +
        "key='" + key + '\'' +
        ", type='" + type + '\'' +
        ", label='" + label + '\'' +
        ", params=" + params.toString() +
        '}';
  }
}
