package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.download.StudentDownloadService
import de.nils_beyer.android.Vertretungen.storage.StudentStorage
import java.io.Serializable

abstract class Account<T : Dataset> {
    // General
    abstract fun getTitle(context : Context) : String

    // Authorization functions
    abstract fun tryRegister(context : Context, username : String, password: String) : Boolean
    abstract fun isRegistered(context : Context) : Boolean
    abstract fun logout(context : Context)
    abstract fun generateHTTPHeaderAuthorization(context : Context) : String

    abstract fun setLoginValid(context: Context, valid: Boolean)
    abstract fun isLoginValid(context: Context): Boolean


    // Datasets
    abstract fun getAvailableDatasets() : Array<T>
    abstract fun containsData(context : Context) : Boolean

    // Download
    abstract fun startDownload(context : Context, returnIntent : PendingIntent)

}

interface Dataset : Serializable {
    fun getURL() : String
    fun getData(context : Context) : GroupCollection
    fun getMOTD(context : Context) : String?
}