package com.alorma.github.bean;

import android.content.Intent;
import com.mikepenz.iconics.typeface.IIcon;

/**
 * Created by Bernat on 26/06/2015.
 */
public class ProfileItem {
  public IIcon icon;
  public String value;
  public Intent intent;

  public ProfileItem(IIcon icon, String value, Intent intent) {
    this.icon = icon;
    this.value = value;
    this.intent = intent;
  }
}
