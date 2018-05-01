package de.nils_beyer.android.Vertretungen.mainActivity

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Browser
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.widget.Toast


import de.nils_beyer.android.Vertretungen.R


class ChromeCustomTabsFAB(context: Context, attrs: AttributeSet) : FloatingActionButton(context, attrs) {
    fun init(tabActivity : TabActivity) {
        setOnClickListener {
            val customtabintent = CustomTabsIntent.Builder()

            // Set color of chrome custom tab
            if (Build.VERSION.SDK_INT >= 23) {
                customtabintent.setToolbarColor(context.getColor(R.color.colorPrimary))
            }
            customtabintent.setShowTitle(true)

            // Transmit header (login credentials) to the chrome custom tab
            val headerArgs = Bundle()
            headerArgs.putString("Authorization", tabActivity.httpHeaderAuthorization)
            customtabintent.build().intent.putExtra(Browser.EXTRA_HEADERS, headerArgs)

            try {
                // Parse URL (as String) to URI
                val uri : Uri? = Uri.parse(tabActivity.selectedURL)

                if (uri != null) {
                    // Call Chrome Custom Tabs
                    customtabintent.build().launchUrl(context, uri)
                } else {
                    Toast.makeText(context, "URL konnte nicht gelesen werden.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Kein Browser installiert", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface TabActivity {
        val selectedURL : String?
        val httpHeaderAuthorization: String
    }
}
