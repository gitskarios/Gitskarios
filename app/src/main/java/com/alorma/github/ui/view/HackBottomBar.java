package com.alorma.github.ui.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;

/**
 * See <a href="https://github.com/roughike/BottomBar/issues/571">https://github.com/roughike/BottomBar/issues/571</a>
 */
public class HackBottomBar extends BottomBar {

  public HackBottomBar(Context context) {
    super(context);
  }

  public HackBottomBar(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    // HACK: in order to avoid a crash in onRestoreInstanceState when badges are present
    // remove badges here
    for (int i = 0; i < getTabCount(); i++) {
      BottomBarTab tab = getTabAtPosition(i);
      tab.removeBadge();
    }

    return super.onSaveInstanceState();
  }
}