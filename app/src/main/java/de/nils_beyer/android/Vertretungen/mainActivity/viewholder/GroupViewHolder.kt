package de.nils_beyer.android.Vertretungen.mainActivity.viewholder

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.Group
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.data.TeacherGroup
import de.nils_beyer.android.Vertretungen.detailActivity.DetailActivity

class GroupViewHolder(v: View, val context : Context) : RecyclerView.ViewHolder(v) {
    var className = v.findViewById<View>(R.id.text_class_name) as TextView
    var replacementCounter = v.findViewById<View>(R.id.text_replacement_count) as TextView
    var cardView = v.findViewById<View>(R.id.overview_card) as CardView
    var marked = v.findViewById<View>(R.id.image_class_marked) as ImageView

    fun bind(groupCollection : GroupCollection, group : Group) {
        if (group is TeacherGroup) {
            className.text = String.format(context.getString(R.string.overview_adapter_group_teacher), group.name)
        } else {
            className.text = String.format(context.getString(R.string.overview_adapter_group_student), group.name)
        }

        if (group.replacements.size > 1)
            replacementCounter.text = String.format(context.getString(R.string.overview_adapter_entry_multiple), group.replacements.size.toString())
        else
            replacementCounter.text = String.format(context.getString(R.string.overview_adapter_entry_single), group.replacements.size.toString())

        marked.visibility = if (group.isMarked(context)) View.VISIBLE else View.GONE
        cardView.setOnClickListener { DetailActivity.startActivity(context, groupCollection, position) }
    }
}