package com.alorma.github.ui.fragment.menu;

public interface OnMenuItemSelectedListener {
  boolean onProfileSelected();

  boolean onReposSelected();

  boolean onPeopleSelected();

  boolean onUserEventsSelected();

  boolean onSettingsSelected();

  boolean onAboutSelected();
}