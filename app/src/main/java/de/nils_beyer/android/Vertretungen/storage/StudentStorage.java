package de.nils_beyer.android.Vertretungen.storage;

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

import de.nils_beyer.android.Vertretungen.data.Group;
import de.nils_beyer.android.Vertretungen.data.GroupCollection;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;



public class StudentStorage implements Serializable{
    public enum source {Today, Tomorrow};

    private static final String KEY_PREFERENCE_STORAGE = "KEY_PREFERENCE_STORAGE";

    private static final String KEY_DATE_TODAY = "KEY_DATE_TODAY";
    private static final String KEY_DATE_TOMORROW = "KEY_DATE_TOMORROW";
    private static final String KEY_IMMEDIACY_TODAY = "KEY_IMMEDIACY_TODAY";
    private static final String KEY_IMMEDIACY_TOMORROW = "KEY_IMMEDIACY_TOMORROW";
    private static final String KEY_DATASET_TODAY = "KEY_DATASET_TODAY";
    private static final String KEY_DATASET_TOMORROW = "KEY_DATASET_TOMORROW";
    private static final String KEY_MOTD_TODAY = "KEY_MOTD_TODAY";
    private static final String KEY_MOTD_TOMORROW = "KEY_MOTD_TOMORROW";

    public static void save(Context context, ArrayList<Group> today, ArrayList<Group> tomorrow, Date dateToday, Date dateTomorrow, Date immediacityToday, Date immediacitryTomorrow, String motd_today, String motd_tomorrow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(KEY_DATE_TODAY, dateToday.getTime());
        editor.putLong(KEY_DATE_TOMORROW, dateTomorrow.getTime());
        editor.putLong(KEY_IMMEDIACY_TODAY, immediacityToday.getTime());
        editor.putLong(KEY_IMMEDIACY_TOMORROW, immediacitryTomorrow.getTime());

        editor.putStringSet(KEY_DATASET_TODAY, parseSetToJson(today));

        editor.putStringSet(KEY_DATASET_TOMORROW, parseSetToJson(tomorrow));

        editor.putString(KEY_MOTD_TODAY, motd_today);
        editor.putString(KEY_MOTD_TOMORROW, motd_tomorrow);

        editor.commit();
    }

    private static Set<String> parseSetToJson(ArrayList<Group>  groupArrayList) {
        Set<String> klassesString = new HashSet<>();

        for (Group k : groupArrayList) {
            klassesString.add(new Gson().toJson(k));
        }

        return klassesString;
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

    public static GroupCollection getTodaySource(final Context context) {
        Date date = getDateToday(context);
        Date immediacity = getImmediacityToday(context);
        ArrayList<Group> groupArrayList = getToday(context);

        return new GroupCollection(date, immediacity, groupArrayList);
    }

    public static GroupCollection getTomorrowSource(final Context context) {
        Date date = getDateTomorrow(context);
        Date immediacity = getImmediacityTomorrow(context);
        ArrayList<Group> groupArrayList = getTomorrow(context);

        return new GroupCollection(date, immediacity, groupArrayList);
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
        if (sharedPreferences.contains(KEY_IMMEDIACY_TODAY))
            return new Date(sharedPreferences.getLong(KEY_IMMEDIACY_TODAY, 0));
        else
            return null;
    }

    public static Date getImmediacityTomorrow(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_IMMEDIACY_TOMORROW))
            return new Date(sharedPreferences.getLong(KEY_IMMEDIACY_TOMORROW, 0));
        else
            return null;
    }

    public static String getMOTDToday(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_MOTD_TODAY)) {
            return sharedPreferences.getString(KEY_MOTD_TODAY, null);
        } else {
            return null;
        }
    }

    public static String getMOTDTomorrow(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(KEY_MOTD_TOMORROW)) {
            return sharedPreferences.getString(KEY_MOTD_TOMORROW, null);
        } else {
            return null;
        }
    }

    public static boolean containsData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);

        return  sharedPreferences.contains(KEY_IMMEDIACY_TODAY) &&
                sharedPreferences.contains(KEY_IMMEDIACY_TOMORROW) &&
                sharedPreferences.contains(KEY_DATE_TODAY) &&
                sharedPreferences.contains(KEY_DATE_TOMORROW) &&
                sharedPreferences.contains(KEY_DATASET_TODAY) &&
                sharedPreferences.contains(KEY_DATASET_TOMORROW);
    }

    public static void sort(final Context c, ArrayList<? extends Group> list) {
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
