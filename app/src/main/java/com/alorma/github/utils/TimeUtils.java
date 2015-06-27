package com.alorma.github.utils;

import android.content.Context;

import com.alorma.github.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bernat on 10/04/2015.
 */
public class TimeUtils {

    public static String getDateToText(Context context, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.at_date_format), Locale.getDefault());

        return context.getString(R.string.joined_at, sdf.format(date));
    }

    public static String getTimeAgoString(Context context, String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DateTime dt = formatter.parseDateTime(date);
        DateTime dtNow = DateTime.now().withZone(DateTimeZone.UTC);

        Years years = Years.yearsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
        int text = R.string.time_ago_at_years;
        int time = years.getYears();

        if (time == 0) {
            Months months = Months.monthsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
            text = R.string.time_ago_at_months;
            time = months.getMonths();

            if (time == 0) {

                Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
                text = R.string.time_ago_at_days;
                time = days.getDays();

                if (time == 0) {
                    Hours hours = Hours.hoursBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                    time = hours.getHours();
                    text = R.string.time_ago_at_hours;

                    if (time == 0) {
                        Minutes minutes = Minutes.minutesBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                        time = minutes.getMinutes();
                        text = R.string.time_ago_at_minutes;
                        if (time == 0) {
                            Seconds seconds = Seconds.secondsBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                            time = seconds.getSeconds();
                            if (time > 5) {
                                text = R.string.time_ago_at_seconds;
                            } else {
                                text = R.string.time_ago_just_now;
                            }
                        }
                    }
                }
            }
        }

        return context.getResources().getString(text, time);
    }
}
