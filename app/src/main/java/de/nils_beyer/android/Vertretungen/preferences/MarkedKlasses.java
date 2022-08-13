package de.nils_beyer.android.Vertretungen.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

import de.nils_beyer.android.Vertretungen.util.ClassNameConverterKt;
import de.nils_beyer.android.Vertretungen.widget.VertretungenWidgetProvider;

/**
 * Created by nbeye on 22. Jan. 2017.
 */

public class MarkedKlasses {
    public static final String KEY_MARKED_KLASSEN = "de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses";
    public static final String KEY_CLASS_NAME_CONVERSION = "de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses.ClassNameConversion";

    public static boolean isMarked(Context application, String name) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_KLASSEN, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(KEY_CLASS_NAME_CONVERSION, true)) {
            convertMarkedKlasses(application);
        }
        return sharedPreferences.getBoolean(name, false);
    }

    private static void convertMarkedKlasses(Context application) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_KLASSEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> allEntries = sharedPreferences.getAll().keySet();
        for (String entry : allEntries) {
            String convertedName = ClassNameConverterKt.convertClassName(entry);
            if (!entry.equals(convertedName)) {
                editor.putBoolean(convertedName, sharedPreferences.getBoolean(entry, false));
            }
        }

        editor.putBoolean(KEY_CLASS_NAME_CONVERSION, false);
        editor.apply();
    }

    public static void setMarked(Context application, String name, boolean marked) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_KLASSEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (marked)
            editor.putBoolean(name, marked);
        else
            editor.remove(name);

        editor.apply();

        VertretungenWidgetProvider.updateWidgetData(application);
    }

    public static boolean hasMarked(Context appContext) {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(KEY_MARKED_KLASSEN, Context.MODE_PRIVATE);
        return sharedPreferences.getAll().size() > 0;
    }
}
