package com.alorma.yaml.formbuilder.field;

import com.alorma.yaml.formbuilder.YamlFormAdapter;
import com.alorma.yaml.formbuilder.YamlFormObject;

public class FormField<K> {

  private YamlFormObject formObject;
  private YamlFormAdapter.FormHolder<K> holder;

  public FormField(YamlFormObject formObject) {
    this.formObject = formObject;
  }

  public YamlFormObject getFormObject() {
    return formObject;
  }

  public K getValue() {
    return holder.getValue();
  }

  public void setHolder(YamlFormAdapter.FormHolder<K> holder) {
    this.holder = holder;
  }
}
