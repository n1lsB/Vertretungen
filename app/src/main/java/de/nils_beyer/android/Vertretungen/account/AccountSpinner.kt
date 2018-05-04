package de.nils_beyer.android.Vertretungen.account

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

import java.util.ArrayList

import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.download.StudentDownloadService
import de.nils_beyer.android.Vertretungen.download.TeacherDownloadService
import de.nils_beyer.android.Vertretungen.storage.StudentStorage
import de.nils_beyer.android.Vertretungen.storage.TeacherStorage

/**
 * Created by nbeye on 02. Okt. 2017.
 */

class AccountSpinner(context: Context, attrs: AttributeSet) : AppCompatSpinner(context, attrs) {


    enum class ViewConfig {
        SHOW_REGISTERED, SHOW_UNREGISTERED
    }

    private var selectedAccount: Account<out Dataset>? = null
    private var accountChangeListener: onAccountChangeListener? = null
    private var viewConfig = ViewConfig.SHOW_REGISTERED


    init {
        init()
    }

    fun setup(onAccountChangeListener: onAccountChangeListener) {
        accountChangeListener = onAccountChangeListener
    }

    fun setViewConfig(config: ViewConfig) {
        viewConfig = config
        init()
    }

    fun init() {
        val accountList = filterAccounts(viewConfig)


        val arrayAdapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                accountList.map { it.getTitle(context) }
        )
        adapter = arrayAdapter

        selectedAccount = if (accountList.size > 0) accountList[0] else null

        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // TODO: could do better by using the array adapter with the account objects
                selectedAccount = accountList.filter { it.getTitle(context) == selectedItem as String }[0]

                if (accountChangeListener != null) {
                    accountChangeListener!!.onAccountChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    /**
     * Update the array adapter of the account spinner to handle changes, like logging out/logging in.
     */
    fun updateAccountSpinner() {
        val accountList = filterAccounts(viewConfig)
        // check if update is needed
        for (i in accountList.indices) {
            if (adapter.count - 1 < i) {
                init()
                break
            }

            if (accountList[i] != adapter.getItem(i)) {
                init()
                break
            }
        }
    }

    fun getSelectedAccount(): Account<*> {
        return selectedAccount ?: EmptyAccount()
    }

    /**
     * Apply a view config on the list of available accounts
     */
    private fun filterAccounts(viewConfig : ViewConfig) : List<Account<out Dataset>> {
        return when(viewConfig) {
            ViewConfig.SHOW_REGISTERED -> availableAccounts.filter { it.isRegistered(context) }
            ViewConfig.SHOW_UNREGISTERED -> availableAccounts.filter { !it.isRegistered(context) }
            else -> throw RuntimeException("not yet implemented")
        }
    }


    fun hasOnlyUnregistered(context : Context) : Boolean {
        return availableAccounts.none { it.isRegistered(context) }
    }

    fun hasOnlyRegistered(context : Context) : Boolean {
        return availableAccounts.none { !it.isRegistered(context) }
    }

    interface onAccountChangeListener {
        fun onAccountChanged()
    }
}
