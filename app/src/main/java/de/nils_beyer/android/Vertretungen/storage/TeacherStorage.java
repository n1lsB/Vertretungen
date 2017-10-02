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
import de.nils_beyer.android.Vertretungen.data.TeacherGroup;
import de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses;


public class TeacherStorage implements Serializable {
    public enum source {Today, Tomorrow};

    private static final String KEY_PREFERENCE_STORAGE = "KEY_PREFERENCE_STORAGE_TEACHER";

    private static final String KEY_DATE_TODAY = "KEY_DATE_TODAY";
    private static final String KEY_DATE_TOMORROW = "KEY_DATE_TOMORROW";
    private static final String KEY_IMMEDIACY_TODAY = "KEY_IMMEDIACY_TODAY";
    private static final String KEY_IMMEDIACY_TOMORROW = "KEY_IMMEDIACY_TOMORROW";
    private static final String KEY_DATASET_TODAY = "KEY_DATASET_TODAY";
    private static final String KEY_DATASET_TOMORROW = "KEY_DATASET_TOMORROW";

    public static void save(Context context, GroupCollection today, GroupCollection tomorrow) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(KEY_DATE_TODAY, today.getDate().getTime());
        editor.putLong(KEY_DATE_TOMORROW, tomorrow.getDate().getTime());
        editor.putLong(KEY_IMMEDIACY_TODAY, today.getImmediacity().getTime());
        editor.putLong(KEY_IMMEDIACY_TOMORROW, tomorrow.getImmediacity().getTime());


        editor.putStringSet(KEY_DATASET_TODAY, parseSetToJson((ArrayList<TeacherGroup>) today.getGroupArrayList()));

        editor.putStringSet(KEY_DATASET_TOMORROW, parseSetToJson((ArrayList<TeacherGroup>) tomorrow.getGroupArrayList()));

        editor.commit();
    }

    private static Set<String> parseSetToJson(ArrayList<TeacherGroup>  groupArrayList) {
        Set<String> klassesString = new HashSet<>();

        for (Group k : groupArrayList) {
            klassesString.add(new Gson().toJson(k));
        }

        return klassesString;
    }

    public static ArrayList<TeacherGroup> getToday(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        Set<String> input = sharedPreferences.getStringSet(KEY_DATASET_TODAY, new HashSet<String>());
        ArrayList<TeacherGroup> klasses = new ArrayList<TeacherGroup>();
        for (String s : input) {
            TeacherGroup k = new Gson().fromJson(s, TeacherGroup.class);
            klasses.add(k);
        }

        sort(klasses);

        return klasses;
    }

    public static ArrayList<TeacherGroup> getTomorrow(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);
        Set<String> input = sharedPreferences.getStringSet(KEY_DATASET_TOMORROW, new HashSet<String>());
        ArrayList<TeacherGroup> klasses = new ArrayList<TeacherGroup>();
        for (String s : input) {
            TeacherGroup k = new Gson().fromJson(s, TeacherGroup.class);
            klasses.add(k);
        }

        sort(klasses);

        return klasses;
    }

    public static GroupCollection getTodaySource(final Context context) {
        Date date = getDateToday(context);
        Date immediacity = getImmediacityToday(context);
        ArrayList<TeacherGroup> groupArrayList = getToday(context);

        return new GroupCollection(date, immediacity, groupArrayList);
    }

    public static GroupCollection getTomorrowSource(final Context context) {
        Date date = getDateTomorrow(context);
        Date immediacity = getImmediacityTomorrow(context);
        ArrayList<TeacherGroup> groupArrayList = getTomorrow(context);

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

    public static boolean containsData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE);

        return  sharedPreferences.contains(KEY_IMMEDIACY_TODAY) &&
                sharedPreferences.contains(KEY_IMMEDIACY_TOMORROW) &&
                sharedPreferences.contains(KEY_DATE_TODAY) &&
                sharedPreferences.contains(KEY_DATE_TOMORROW) &&
                sharedPreferences.contains(KEY_DATASET_TODAY) &&
                sharedPreferences.contains(KEY_DATASET_TOMORROW);
    }

    public static void sort(ArrayList<TeacherGroup> list) {
        Collections.sort(list, new Comparator<Group>() {
            @Override
            public int compare(Group o1, Group o2) {
                return o1.name.compareTo(o2.name);
            }
        });
    }
}
