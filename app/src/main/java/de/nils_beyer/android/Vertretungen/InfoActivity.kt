package de.nils_beyer.android.Vertretungen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import de.nils_beyer.android.Vertretungen.account.Account
import de.nils_beyer.android.Vertretungen.account.Dataset
import de.nils_beyer.android.Vertretungen.account.availableAccounts
import de.nils_beyer.android.Vertretungen.events.EventStorage
import de.nils_beyer.android.Vertretungen.preferences.MarkedCoursesActivity

import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.content_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.info_title)

        // Setup back icon
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.contentInsetStartWithNavigation = 0
        supportActionBar!!.setHomeButtonEnabled(true)

        // Setup App title with version name
        val version = getAppVersion()
        if (version != null) {
            info_app_name.text = String.format(getString(R.string.info_app_with_version), version)

            // Setup BuildVariant Label
            val buildVariant = findViewById<View>(R.id.info_build_variant) as TextView
            when {
                version.endsWith("D") -> buildVariant.text = String.format(getString(R.string.info_warning_debug), "DEBUG")
                version.endsWith("R") -> buildVariant.text = String.format(getString(R.string.info_warning_debug), "RELEASE")
                else -> buildVariant.visibility = View.GONE
            }
        }

        // Link to license Activity
        info_button_license.setOnClickListener {
            val startActivity = Intent(this@InfoActivity, LicenseActivity::class.java)
            startActivity(startActivity)
        }

        // implement recycler view
        info_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AccountListAdapter(context, this@InfoActivity)
            // We pass the InfoActivity so that the Adapter can call checkAccountStatus to update the button
        }


        info_button_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        checkAccountStatus()

        info_button_marked_courses.setOnClickListener {
            startActivity(Intent(this, MarkedCoursesActivity::class.java))
        }

        // Setup event config edit texts
        editText_events_config_param.text = EventStorage.getConfigParamName(applicationContext).toEditable()
        editText_events_config_value.text = EventStorage.getConfigParamValue(applicationContext).toEditable()

        editText_events_config_param.afterTextChanged {
            EventStorage.saveConfigParamName(applicationContext, it)
        }
        editText_events_config_value.afterTextChanged {
            EventStorage.saveConfigParamValue(applicationContext, it)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAccountStatus()
        (info_recyclerview.adapter as AccountListAdapter).update()
    }

    /**
     * Refresh the status of the login button: When no account is left to login,
     * we should hide the button to prevent unnecessary actions/calls
     */
    fun checkAccountStatus() {
        if (availableAccounts.any { !it.isRegistered(applicationContext) }) {
            info_button_login.visibility = View.VISIBLE
        } else {
            info_button_login.visibility = View.GONE
        }
    }


    /**
     * Returns the app version name or null if packageManager throws an error
     */
    private fun getAppVersion() : String? {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }


    private class AccountListAdapter(val context : Context, val infoActivity : InfoActivity) : RecyclerView.Adapter<AccountListAdapter.AccountListViewHolder>() {
        var accounts = update()

        private class AccountListViewHolder(val v: View, val context : Context, val adapter : AccountListAdapter) : RecyclerView.ViewHolder(v) {
            val text1 = v.findViewById<TextView>(android.R.id.text1)
            val text2 = v.findViewById<TextView>(android.R.id.text2)

            fun bind(account : Account<out Dataset>) {
                text1.text = account.getTitle(context)
                text2.text = "Klicken zum Abmelden"
                v.isClickable = true
                v.setOnClickListener {
                    account.logout(context)
                    adapter.update()
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): AccountListViewHolder {
            val v = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_2, viewGroup, false)

            return AccountListViewHolder(v, context, this)
        }

        override fun getItemCount(): Int = accounts.size

        override fun onBindViewHolder(viewHolder: AccountListViewHolder, position: Int) {
            viewHolder.bind(accounts[position])
        }

        fun update() : List<Account<out Dataset>> {
            accounts = availableAccounts.filter { it.isRegistered(context) }
            notifyDataSetChanged()
            infoActivity.checkAccountStatus()
            return accounts
        }
    }
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

