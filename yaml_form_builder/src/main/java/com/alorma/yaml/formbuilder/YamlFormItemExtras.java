package com.alorma.yaml.formbuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) public class YamlFormItemExtras {
  @JsonProperty("emojis")
  public boolean emojis;
  @JsonProperty("images")
  public boolean images;
  @JsonProperty("source")
  public boolean source;

  @Override
  public String toString() {
    return "YamlFormItemExtras{" +
        "source=" + source +
        ", images=" + images +
        ", emojis=" + emojis +
        '}';
  }
}
