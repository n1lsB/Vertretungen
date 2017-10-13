package de.nils_beyer.android.Vertretungen.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by nbeye on 22. Jan. 2017.
 */

public class MarkedCourses {
    public static final String KEY_MARKED_COURSES = "de.nils_beyer.android.Vertretungen.preferences.MarkedCourses";

    public static boolean isMarked(Context application, String klasse, String kurs) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(handleInputString(klasse) + "\\" + handleInputString(kurs), false);
    }

    public static boolean isKlausurMarked(Context application, String klasse, String info_klausur) {
        info_klausur = info_klausur.toLowerCase();
        if (info_klausur.startsWith("gk ")) {
            String kurse = info_klausur.replaceFirst("gk ", "");
            String[] kursList = kurse.split("\\+");
            for (String kurs : kursList) {
                kurs = kurs.trim();
                String kursname;
                if (kurs.matches("^f\\d")) {
                    if (kurs.matches("gk\\d$")) {
                        kursname = kurs;
                    } else {
                        kursname = kurs + "gk1";
                    }
                } else {
                    if (kurs.matches("[a-zäöü]+\\d$")) {
                        kursname = kurs.substring(0, kurs.length() - 1) + "gk" + kurs.substring(kurs.length() - 1);
                    } else {
                        kursname = kurs + "gk1";
                    }
                }
                if (isMarked(application, klasse, kursname)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setMarked(Context application, String klasse, String kurs, boolean marked) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (marked)
            editor.putBoolean(handleInputString(klasse) + "\\" + handleInputString(kurs), marked);
        else
            editor.remove(handleInputString(klasse) + "\\" + handleInputString(kurs));

        editor.apply();
    }

    public static void deleteEntry(Context application, String entry) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(entry);
        editor.apply();
    }

    public static boolean hasMarked(Context appContext) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);
        return sharedPreferences.getAll().size() > 0;
    }

    public static Set<String> returnList(Context appContext) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);
        return sharedPreferences.getAll().keySet();
    }

    public static void removeAll(Context appContext) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(KEY_MARKED_COURSES, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    public static String handleInputString(String input) {
        return input.toUpperCase().replace("-", "").trim();
    }
}
