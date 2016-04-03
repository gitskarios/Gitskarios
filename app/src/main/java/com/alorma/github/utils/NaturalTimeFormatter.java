package com.alorma.github.utils;

import android.content.Context;
import tk.zielony.naturaldateformat.AbsoluteDateFormat;
import tk.zielony.naturaldateformat.NaturalDateFormat;
import tk.zielony.naturaldateformat.RelativeDateFormat;

public class NaturalTimeFormatter implements TimeFormatter {
  private Context context;

  public NaturalTimeFormatter(Context context) {
    this.context = context;
  }

  @Override
  public String relative(long milis) {
    RelativeDateFormat relFormat = new RelativeDateFormat(context, NaturalDateFormat.TIME);
    return relFormat.format(milis);
  }

  @Override
  public String absolute(long milis) {
    AbsoluteDateFormat absFormat = new AbsoluteDateFormat(context,
        NaturalDateFormat.DATE | NaturalDateFormat.HOURS | NaturalDateFormat.MINUTES);
    return absFormat.format(milis);
  }
}
