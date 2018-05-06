package de.nils_beyer.android.Vertretungen.mainActivity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.GroupViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.ImmediacityViewHolder

class OverviewAdapter(private val context: Context, private var groupCollection: GroupCollection) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    public enum class ViewTypes { GroupViewHolder, ImmediacityViewHolder }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            holder.bind(groupCollection, groupCollection.groupArrayList[position - 1])
        } else if (holder is ImmediacityViewHolder) {
            holder.bind(groupCollection.immediacity)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return ViewTypes.ImmediacityViewHolder.ordinal
        } else {
            return ViewTypes.GroupViewHolder.ordinal
        }
    }

    override fun getItemCount(): Int {
        return if (groupCollection.groupArrayList.size == 0) {
            0
        } else {
            groupCollection.groupArrayList.size + 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ViewTypes.ImmediacityViewHolder.ordinal) {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_overview_immediacity, parent, false)
            val vh = ImmediacityViewHolder(v, context)
            return vh
        } else if (viewType == ViewTypes.GroupViewHolder.ordinal) {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_overview_layout, parent, false)
            val vh = GroupViewHolder(v, context)
            return vh
        } else {
            TODO("not yet implemented?!")
        }
    }

    fun update(collection: GroupCollection?) {
        if (collection == null) {
            return
        }
        this.groupCollection = collection
        notifyDataSetChanged()
    }
}