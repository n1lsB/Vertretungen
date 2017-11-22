package de.nils_beyer.android.Vertretungen.widget;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * A special Storage Class used to Store
 * the Configuration of an Widget inside SharedPreferences.
 *
 * This is necessary beacause the AppWidgetManager's methods
 * don't store Configuration data persistently, so the config is deleted
 * on each device reboot
 *
 * Created 22. Nov 2017 by nbeyer
 */
public class WidgetConfigurationStorage {
    private static final String STORAGE_KEY = "WIDGET_CONFIG_STORAGE_KEY";

    /**
     * Store the given Configuration inside the SharedPreferences Storage
     * @param c         Context used to get access to the SharedPreferences API
     * @param widgetId  The WidgetID for which the configuration is set
     * @param accountId The Configuration for the widgetId - the ID of the selected account
     */
    public static void setConfig(Context c, int widgetId, int accountId) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(String.valueOf(widgetId), accountId)
                .commit();
    }

    /**
     * Return the stored Configuration inside the SharedPreferences Storage
     * @param c         Context used to get access to the SharedPreferences API
     * @param widgetId  The widgetId of the widget, which config should be returned
     * @return          the Configuration (accountId) or -1, if no configuration is stored.
     */
    public static int getConfig(Context c, int widgetId) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(String.valueOf(widgetId), -1);
    }
}
