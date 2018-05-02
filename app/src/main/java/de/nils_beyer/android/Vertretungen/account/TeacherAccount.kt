package de.nils_beyer.android.Vertretungen.account

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

/**
 * Created by nbeye on 22. Mai. 2017.
 */

object TeacherAccount {
    private const val KEY_PREFERENCE_ACCOUNT = "KEY_PREFERENCE_ACCOUNT_TEACHER"
    private const val KEY_USER_NAME = "KEY_USER_NAME"
    private const val KEY_PASSWORD = "KEY_PASSWORD"

    @JvmStatic
    fun getUserName(c: Context): String? {
        sharedPreferences(c) {
            return it.getString(KEY_USER_NAME, null)
        }
    }

    @JvmStatic
    fun getPassword(c: Context): String? {
        return sharedPreferences(c) {
            it.getString(KEY_PASSWORD, null)
        }
    }

    @JvmStatic
    fun register(c: Context, _username: String, _password: String) {
        sharedPreferences(c) {
            it.edit()
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

    @JvmStatic
    fun generateHTTPHeaderAuthorization(username: String?, password: String?): String {
        return "Basic " + Base64.encodeToString(
                "$username:$password".toByteArray(), Base64.DEFAULT)
    }

    @JvmStatic
    fun generateHTTPHeaderAuthorization(c: Context): String {
        return generateHTTPHeaderAuthorization(getUserName(c), getPassword(c))
    }

    private inline fun <T> sharedPreferences(context : Context, body: (sharedPreferences : SharedPreferences) -> (T)) : T {
        val sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE)
        return body(sharedPreferences)
    }
}
