package de.nils_beyer.android.Vertretungen.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

/**
 * Created by nbeye on 22. Mai. 2017.
 */

object StudentAccount {
    private const val KEY_PREFERENCE_ACCOUNT = "KEY_PREFERENCE_ACCOUNT"
    private const val KEY_USER_NAME = "KEY_USER_NAME"
    private const val KEY_PASSWORD = "KEY_PASSWORD"

    @JvmStatic
    fun getUserName(c: Context): String? {
        return sharedPreferences(c) { sharedPreferences ->
            sharedPreferences.getString(KEY_USER_NAME, null)
        }
    }

    @JvmStatic
    fun getPassword(c: Context): String? {
        return sharedPreferences(c) { sharedPreferences ->
            sharedPreferences.getString(KEY_PASSWORD, null)
        }
    }

    @JvmStatic
    fun register(c: Context, _username: String, _password: String) {
        sharedPreferences(c) { sharedPreferences ->
            sharedPreferences.edit()
                    .putString(KEY_PASSWORD, _password)
                    .putString(KEY_USER_NAME, _username)
                    .apply()
        }
    }

    @JvmStatic
    fun isRegistered(c: Context): Boolean {
        return sharedPreferences(c) {
            it.contains(KEY_USER_NAME) && it.contains(KEY_PASSWORD)
        }
    }

    @JvmStatic
    fun logout(c: Context) {
        sharedPreferences(c) {
            it.edit().clear().apply()
        }
    }

    /**
     * Generate a HTTP Header authorization property for a given username, password
     */
    @JvmStatic
    fun generateHTTPHeaderAuthorization(username: String?, password: String?): String {
        return "Basic " + Base64.encodeToString(
                "$username:$password".toByteArray(), Base64.DEFAULT)
    }

    /**
     * Generate a HTTP Header authorization property for the saved username/password
     */
    @JvmStatic
    fun generateHTTPHeaderAuthorization(c: Context): String =
        generateHTTPHeaderAuthorization(getUserName(c), getPassword(c))


    /**
     * Inline function to access shared preferences storage
     */
    private inline fun <T> sharedPreferences(c : Context, body: (SharedPreferences) -> T) : T {
        val sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE)
        return body(sharedPreferences)
    }
}
