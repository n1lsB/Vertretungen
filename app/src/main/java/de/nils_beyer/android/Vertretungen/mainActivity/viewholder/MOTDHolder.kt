package de.nils_beyer.android.Vertretungen.mainActivity.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.util.DateParser
import java.text.SimpleDateFormat
import java.util.*


class MOTDHolder(val v: View, val context : Context) : RecyclerView.ViewHolder(v) {
    val motdText = v.findViewById<TextView>(R.id.text_motd)


    internal fun bind(motd : String) {
        motdText.text = motd
    }
}