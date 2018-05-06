package de.nils_beyer.android.Vertretungen.mainActivity.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.util.DateParser
import java.text.SimpleDateFormat
import java.util.*


class ImmediacityViewHolder(val v: View, val context : Context) : RecyclerView.ViewHolder(v) {
    val immediacityText = v.findViewById<TextView>(R.id.text_immediacity)


    internal fun bind(immediacity : Date) {
        val simpleDateFormat = SimpleDateFormat(context.getString(R.string.date_format_only_time))

        immediacityText.text = String.format(
                context.getString(R.string.overview_adapter_immediacity),
                DateParser.parseDateToShortString(context, immediacity),
                simpleDateFormat.format(immediacity)
        )
    }
}