package com.alorma.github.utils;

public interface TimeFormatter {
  String relative(long milis);
  String absolute(long milis);
}
