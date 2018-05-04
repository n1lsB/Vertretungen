package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Base64
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.download.StudentDownloadService
import de.nils_beyer.android.Vertretungen.storage.StudentStorage
import kotlin.concurrent.thread

/**
 * Created by nbeye on 22. Mai. 2017.
 */

object StudentAccount : Account<StudentAccount.StudentDatasets>() {
    enum class StudentDatasets : Dataset {
        Today {
            override fun getURL(): String = StudentDownloadService.URL_TODAY
            override fun getData(context : Context): GroupCollection = StudentStorage.getTodaySource(context)
        },
        Tomorrow {
            override fun getURL(): String = StudentDownloadService.URL_TOMORROW
            override fun getData(context : Context): GroupCollection = StudentStorage.getTomorrowSource(context)
        }
    }


    private const val KEY_PREFERENCE_ACCOUNT = "KEY_PREFERENCE_ACCOUNT"
    private const val KEY_USER_NAME = "KEY_USER_NAME"
    private const val KEY_PASSWORD = "KEY_PASSWORD"

    override fun getTitle(context: Context): String {
        return context.getString(R.string.account_name_student)
    }

    override fun containsData(context : Context): Boolean {
        return StudentStorage.containsData(context)
    }

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
    override fun generateHTTPHeaderAuthorization(c: Context): String =
        generateHTTPHeaderAuthorization(getUserName(c), getPassword(c))


    /**
     * Inline function to access shared preferences storage
     */
    private inline fun <T> sharedPreferences(c : Context, body: (SharedPreferences) -> T) : T {
        val sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_ACCOUNT, Context.MODE_PRIVATE)
        return body(sharedPreferences)
    }


    override fun getAvailableDatasets(): Array<StudentDatasets> = StudentDatasets.values()

    /**
     * Starts the download service and passes the given pending intent to that service
     * @param returnIntent pending intent that will be called by the download service
     */
    override fun startDownload(context : Context, returnIntent : PendingIntent) {
        val intent = Intent(context, StudentDownloadService::class.java)
        intent.putExtra(StudentDownloadService.PENDING_RESULT_EXTRA, returnIntent)
        context.startService(intent)
    }

    /**
     * Tries to verify the credentials by downloading the HTML file.
     * @return true, if download was successful, else false.
     */
    override fun tryRegister(context: Context, username: String, password: String): Boolean {
        return try {
            StudentDownloadService.downloadHTMLFile(StudentDatasets.Today.getURL(), username, password)
            register(context, username, password)
            true
        } catch (e : Exception) {
            false
        }
    }
}
