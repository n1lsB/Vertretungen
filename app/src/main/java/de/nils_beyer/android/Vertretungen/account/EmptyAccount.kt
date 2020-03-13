package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import de.nils_beyer.android.Vertretungen.data.Group
import de.nils_beyer.android.Vertretungen.data.GroupCollection

class EmptyAccount : Account<EmptyAccount.EmptyDataset>() {
    class EmptyDataset : Dataset {
        override fun getMOTD(context: Context): String? {
            return null
        }

        override fun getURL(): String {
            return "about:blank"
        }

        override fun getData(context: Context): GroupCollection {
            return GroupCollection(null, null, ArrayList<Group>())
        }
    }

    override fun getTitle(context: Context): String {
        return "EMPTY"
    }

    override fun tryRegister(context: Context, username: String, password: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRegistered(context: Context): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logout(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateHTTPHeaderAuthorization(context: Context): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAvailableDatasets(): Array<EmptyDataset> {
        return arrayOf(EmptyDataset(), EmptyDataset())
    }

    override fun setLoginValid(context: Context, valid: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isLoginValid(context: Context): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsData(context: Context): Boolean = false

    override fun startDownload(context: Context, returnIntent: PendingIntent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}