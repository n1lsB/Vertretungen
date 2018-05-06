package de.nils_beyer.android.Vertretungen.mainActivity.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.events.Event
import kotlinx.android.synthetic.main.recyclerview_overview_event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventViewHolder(val v: View, val context : Context) : RecyclerView.ViewHolder(v) {
    val textTitle = v.findViewById<TextView>(R.id.text_event_title)
    val textTime = v.findViewById<TextView>(R.id.text_event_time)


    internal fun bind(event : Event) {
        textTitle.text = event.title;

        val dateFormat = SimpleDateFormat("dd.MM", Locale.GERMANY)
        var timeString = dateFormat.format(event.start)
        if (event.end != null) {
            timeString += " - " + dateFormat.format(event.end)
        }
        if (event.timeInformation != null) {
            timeString += ": " + event.timeInformation
        }

        textTime.text = timeString
    }
}