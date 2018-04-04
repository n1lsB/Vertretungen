package de.nils_beyer.android.Vertretungen

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

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

}
