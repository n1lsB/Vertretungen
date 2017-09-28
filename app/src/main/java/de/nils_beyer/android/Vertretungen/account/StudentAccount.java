package de.nils_beyer.android.Vertretungen.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by nbeye on 22. Mai. 2017.
 */

public final class StudentAccount {
    private static final String KEY_PREFERENCE_ACCOUNT = "KEY_PREFERENCE_ACCOUNT";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";

    public static String getUserName(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public static String getPassword(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public static void register(Context c, String _username, String _password) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(KEY_PASSWORD, _password)
                .putString(KEY_USER_NAME, _username)
                .commit();
    }


    public static boolean isRegistered(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);

        return sharedPreferences.contains(KEY_USER_NAME) && sharedPreferences.contains(KEY_PASSWORD);
    }

    public static void logout(Context c) {
        SharedPreferences sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

    public static String generateHTTPHeaderAuthorization(String username, String password) {
        return "Basic " + Base64.encodeToString(
                (username + ":" + password).getBytes(), Base64.DEFAULT);
    }

    public static String generateHTTPHeaderAuthorization(Context c) {
        return generateHTTPHeaderAuthorization(getUserName(c), getPassword(c));
    }
}
