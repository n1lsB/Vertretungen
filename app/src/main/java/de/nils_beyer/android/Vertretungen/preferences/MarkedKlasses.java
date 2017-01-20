package de.nils_beyer.android.Vertretungen.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import de.nils_beyer.android.Vertretungen.widget.VertretungenWidgetProvider;

/**
 * Created by nbeye on 22. Jan. 2017.
 */

public class MarkedKlasses {
    public static final String KEY_MARKED_KLASSEN = "de.nils_beyer.android.Vertretungen.preferences.MarkedKlasses";

    public static boolean isMarked(Context application, String name) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(KEY_MARKED_KLASSEN, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, false);
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
