package com.alorma.github.utils;

import android.content.Context;

import com.alorma.github.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.Century;
import org.ocpsoft.prettytime.units.Millennium;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bernat on 10/04/2015.
 */
public class TimeUtils {

    public static String getDateToText(Context context, Date date, int resId) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.at_date_format), Locale.getDefault());

        return context.getString(resId, sdf.format(date));
    }

    public static String getTimeAgoString(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(DateTimeZone.UTC);

        DateTime dt = formatter.parseDateTime(date);
        PrettyTime p = new PrettyTime();

        p.removeUnit(Millennium.class);
        p.removeUnit(Century.class);
        p.removeUnit(Year.class);
        p.removeUnit(Month.class);
        p.removeUnit(Week.class);

        return p.format(new Date(dt.getMillis()));
    }
}
