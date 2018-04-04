package de.nils_beyer.android.Vertretungen

import kotlinx.android.synthetic.main.activity_license.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle


class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.info_title_libraries)

        // Setup navigation button
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_48dp)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.contentInsetStartWithNavigation = 0
        supportActionBar!!.setHomeButtonEnabled(true)
    }
}
