package de.nils_beyer.android.Vertretungen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;



public class DataModel implements Serializable {
    public enum source {Today, Tomorrow};

    private static final String KEY_PREFERENCE_STORAGE = "KEY_PREFERENCE_STORAGE";
    private static final String KEY_DATE_TODAY = "KEY_DATE_TODAY";
    private static final String KEY_DATE_TOMORROW = "KEY_DATE_TOMORROW";
    private static final String KEY_IMMEDIACITY_TODAY = "KEY_IMMEDIACITY_TODAY";
    private static final String KEY_IMMEDIACITY_TOMORROW = "KEY_IMMEDIACITY_TOMORROW";
    private static final String KEY_DATASET_TODAY = "KEY_DATASET_TODAY";
    private static final String KEY_DATASET_TOMORROW = "KEY_DATASET_TOMORROW";

    public static void save(Context context, ArrayList<Group> today, ArrayList<Group> tomorrow, Date dateToday, Date dateTomorrow, Date immediacityToday, Date immediacitryTomorrow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(KEY_DATE_TODAY, dateToday.getTime());
        editor.putLong(KEY_DATE_TOMORROW, dateTomorrow.getTime());
        editor.putLong(KEY_IMMEDIACITY_TODAY, immediacityToday.getTime());
        editor.putLong(KEY_IMMEDIACITY_TOMORROW, immediacitryTomorrow.getTime());

        // Dataset Today
        Set<String> klassesString = new HashSet<>();

        for (Group k : today) {
            klassesString.add(new Gson().toJson(k));
        }

        editor.putStringSet(KEY_DATASET_TODAY, klassesString);

        // Dataset Tomorrow
        klassesString = new HashSet<>();

        for (Group k : tomorrow) {
            klassesString.add(new Gson().toJson(k));
        }

        editor.putStringSet(KEY_DATASET_TOMORROW, klassesString);

        editor.commit();
        Log.d("DATAMODEL", "save: saved");
    }

    public static ArrayList<Group> getToday(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        Set<String> input = sharedPreferences.getStringSet(KEY_DATASET_TODAY, new HashSet<String>());
        ArrayList<Group> klasses = new ArrayList<Group>();
        for (String s : input) {
            Group k = new Gson().fromJson(s, Group.class);
            klasses.add(k);
        }

        sort(context, klasses);

        return klasses;
    }

    public static ArrayList<Group> getTomorrow(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        Set<String> input = sharedPreferences.getStringSet(KEY_DATASET_TOMORROW, new HashSet<String>());
        ArrayList<Group> klasses = new ArrayList<Group>();
        for (String s : input) {
            Group k = new Gson().fromJson(s, Group.class);
            klasses.add(k);
        }

        sort(context, klasses);

        return klasses;
    }

    public static Source getTodaySource(final Context context) {
        Date date = getDateToday(context);
        Date immediacity = getImmediacityToday(context);
        ArrayList<Group> groupArrayList = getToday(context);

        return new Source(date, immediacity, groupArrayList);
    }

    public static Source getTomorrowSource(final Context context) {
        Date date = getDateTomorrow(context);
        Date immediacity = getImmediacityTomorrow(context);
        ArrayList<Group> groupArrayList = getTomorrow(context);

        return new Source(date, immediacity, groupArrayList);
    }

    public static Date getDateToday(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_DATE_TODAY))
            return new Date(sharedPreferences.getLong(KEY_DATE_TODAY, 0));
        else
            return null;
    }

    public static Date getDateTomorrow(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_DATE_TOMORROW))
            return new Date(sharedPreferences.getLong(KEY_DATE_TOMORROW, 0));
        else
            return null;
    }

    public static Date getImmediacityToday(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_IMMEDIACITY_TODAY))
            return new Date(sharedPreferences.getLong(KEY_IMMEDIACITY_TODAY, 0));
        else
            return null;
    }

    public static Date getImmediacityTomorrow(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_IMMEDIACITY_TOMORROW))
            return new Date(sharedPreferences.getLong(KEY_IMMEDIACITY_TOMORROW, 0));
        else
            return null;
    }

    public static boolean containsData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);

        return  sharedPreferences.contains(KEY_IMMEDIACITY_TODAY ) &&
                sharedPreferences.contains(KEY_IMMEDIACITY_TOMORROW) &&
                sharedPreferences.contains(KEY_DATE_TODAY) &&
                sharedPreferences.contains(KEY_DATE_TOMORROW) &&
                sharedPreferences.contains(KEY_DATASET_TODAY) &&
                sharedPreferences.contains(KEY_DATASET_TOMORROW);
    }

    public static void sort(final Context c, ArrayList<Group> list) {
        Collections.sort(list, new Comparator<Group>() {
            @Override
            public int compare(Group o1, Group o2) {
                boolean o1Marked = MarkedKlasses.isMarked(c, o1.name);
                boolean o2Marked = MarkedKlasses.isMarked(c, o2.name);

                if (o1Marked && !o2Marked) {
                    return -1;
                } else if (!o1Marked && o2Marked) {
                    return 1;
                } else {
                    return o1.name.compareTo(o2.name);
                }
            }
        });
    }
}
