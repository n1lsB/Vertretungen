package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import java.util.*

abstract class Account<T : Dataset> {
    // General
    abstract fun getTitle(context : Context) : String

    // Authorization functions
    abstract fun tryRegister(context : Context, username : String, password: String) : Boolean
    abstract fun isRegistered(context : Context) : Boolean
    abstract fun logout(context : Context)
    abstract fun generateHTTPHeaderAuthorization(context : Context) : String

    // Datasets
    abstract fun getAvailableDatasets() : Array<T>
    abstract fun containsData(context : Context) : Boolean

    // Download
    abstract fun startDownload(context : Context, returnIntent : PendingIntent)

}

abstract interface Dataset {
    abstract fun getURL() : String
    abstract fun getData(context : Context) : GroupCollection
}