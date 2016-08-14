package com.alorma.yaml.formbuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class YamlForm {
  @JsonProperty("config") public Object config;
  @JsonProperty("form") public List<YamlFormObject> yamlFormObjects;
}
