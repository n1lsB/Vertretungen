package de.nils_beyer.android.Vertretungen.util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.nils_beyer.android.Vertretungen.R;

/**
 * Util class for parsing Date object and comparing them
 * Created by nbeyer on 25. Feb. 2017.
 */

public class DateParser {
    /**
     * Checks if two date object are referencing to the same day
     * @param date1 first date object to compare
     * @param date2 second date object to compare
     * @return true, when date1 and date2 are referencing to the same day, otherwise false
     */
    public static boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);


    }

    /**
     * Parses a date object to a String
     * @param date the date object to be parsed
     * @return parsed Date
     */
    private static String parseDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM");
        return format.format(date);
    }

    /**
     * Returns the a date object referring to tomorrow
     * @return date object referring to tomorrow
     */
    public static Date getTomorrowDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * Converts a date object to a more natural String. It uses weekday names, "Today", "Tomorrow" and otherwise the format specified in @link{parseDateToString}
     * @param context Context used for String Resources
     * @param date The date object to be converted
     * @return date object parsed into a String
     */
    public static String parseDateToShortString(Context context, Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        if (sameDay(date, new Date())) {
            return context.getString(R.string.today);
        }

        if (date.before(new Date())) {
            calendar.add(Calendar.DATE, -1);
            if (sameDay(calendar.getTime(), date))
                return context.getString(R.string.Yesterday);
            else
                return parseDateToString(date);
        }

        // Calculate the difference between now and the date object in days.
        int dayDifference = 0;
        while (!sameDay(date, calendar.getTime())) {
            dayDifference++;
            calendar.add(Calendar.DATE, 1);
        }

        if (dayDifference == 0) {
            return context.getString(R.string.today);
        }
        if (dayDifference == 1) {
            return context.getString(R.string.tomorrow);
        }

        if (dayDifference > 1 && dayDifference < 7) {
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    return context.getString(R.string.MONDAY);
                case Calendar.TUESDAY:
                    return context.getString(R.string.TUESDAY);
                case Calendar.WEDNESDAY:
                    return context.getString(R.string.WEDNESDAY);
                case Calendar.THURSDAY:
                    return context.getString(R.string.THURSDAY);
                case Calendar.FRIDAY:
                    return context.getString(R.string.FRIDAY);
                case Calendar.SATURDAY:
                    return context.getString(R.string.SATURDAY);
                case Calendar.SUNDAY:
                    return context.getString(R.string.SUNDAY);
                default:
                    return parseDateToString(date);
            }
        } else {
            return parseDateToString(date);
        }

    }
}
