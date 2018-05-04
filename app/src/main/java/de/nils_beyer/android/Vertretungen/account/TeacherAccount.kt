package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Base64
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.download.TeacherDownloadService
import de.nils_beyer.android.Vertretungen.download.downloadHTMLFileWithCredientials
import de.nils_beyer.android.Vertretungen.storage.TeacherStorage

/**
 * Created by nbeye on 22. Mai. 2017.
 */

object TeacherAccount : Account<TeacherAccount.TeacherDatasets>() {
    enum class TeacherDatasets : Dataset {
        Today {
            override fun getURL(): String = TeacherDownloadService.URL_TODAY
            override fun getData(context : Context): GroupCollection = TeacherStorage.getTodaySource(context)
        },
        Tomorrow {
            override fun getURL(): String = TeacherDownloadService.URL_TOMORROW
            override fun getData(context : Context): GroupCollection = TeacherStorage.getTomorrowSource(context)
        }
    }

    private const val KEY_PREFERENCE_ACCOUNT = "KEY_PREFERENCE_ACCOUNT_TEACHER"
    private const val KEY_USER_NAME = "KEY_USER_NAME"
    private const val KEY_PASSWORD = "KEY_PASSWORD"

    override fun getTitle(context: Context): String {
        return context.getString(R.string.account_name_teacher)
    }

    override fun containsData(context: Context): Boolean {
        return TeacherStorage.containsData(context)
    }

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



    override fun isRegistered(c: Context): Boolean {
        return sharedPreferences(c) {
            it.contains(KEY_USER_NAME) && it.contains(KEY_PASSWORD)
        }
    }

    override fun logout(c: Context) {
        sharedPreferences(c) {
            it.edit().clear().apply()
        }
    }

    @JvmStatic
    fun generateHTTPHeaderAuthorization(username: String?, password: String?): String {
        return "Basic " + Base64.encodeToString(
                "$username:$password".toByteArray(), Base64.DEFAULT)
    }

    override fun generateHTTPHeaderAuthorization(c: Context): String {
        return generateHTTPHeaderAuthorization(getUserName(c), getPassword(c))
    }

    private inline fun <T> sharedPreferences(context : Context, body: (sharedPreferences : SharedPreferences) -> (T)) : T {
        val sharedPreferences = context.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE)
        return body(sharedPreferences)
    }

    override fun tryRegister(context: Context, username: String, password: String): Boolean {
        return try {
            downloadHTMLFileWithCredientials(TeacherDatasets.Today.getURL(), username, password)
            register(context, username, password)
            true
        } catch(e : Exception) {
            false
        }
    }

    override fun getAvailableDatasets(): Array<TeacherDatasets> {
        return TeacherDatasets.values()
    }

    /**
     * Starts the download service and passes the given pending intent to that service
     * @param returnIntent pending intent that will be called by the download service
     */
    override fun startDownload(context : Context, returnIntent : PendingIntent) {
        val intent = Intent(context, TeacherDownloadService::class.java)
        intent.putExtra(TeacherDownloadService.PENDING_RESULT_EXTRA, returnIntent)
        context.startService(intent)
    }
}
