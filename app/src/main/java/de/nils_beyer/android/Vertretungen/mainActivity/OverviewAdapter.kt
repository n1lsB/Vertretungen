package de.nils_beyer.android.Vertretungen.mainActivity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.events.Event
import de.nils_beyer.android.Vertretungen.events.EventStorage
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.EventViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.GroupViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.ImmediacityViewHolder

class OverviewAdapter(private val context: Context, private var groupCollection: GroupCollection) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ViewTypes { GroupViewHolder, ImmediacityViewHolder, EventsViewHolder }

    private var events : List<Event>? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupViewHolder -> holder.bind(groupCollection, position - events!!.size - 1)
            is ImmediacityViewHolder -> holder.bind(groupCollection.immediacity)
            is EventViewHolder -> holder.bind(events!![position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        val eventsCount = events!!.size
        return when {
            position < eventsCount -> ViewTypes.EventsViewHolder.ordinal
            position == eventsCount -> ViewTypes.ImmediacityViewHolder.ordinal
            else -> ViewTypes.GroupViewHolder.ordinal
        }
    }

    override fun getItemCount(): Int {
        return if (groupCollection.groupArrayList.size == 0) {
            0 + events!!.size
        } else {
            groupCollection.groupArrayList.size + 1 + events!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ViewTypes.ImmediacityViewHolder.ordinal -> {
                val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_overview_immediacity, parent, false)
                return ImmediacityViewHolder(v, context)
            }
            ViewTypes.GroupViewHolder.ordinal -> {
                val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_overview_layout, parent, false)
                return GroupViewHolder(v, context)
            }
            ViewTypes.EventsViewHolder.ordinal -> {
                val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_overview_event, parent, false)
                return EventViewHolder(v, context)
            }
            else -> TODO("not yet implemented?!")
        }
    }

    fun update(collection: GroupCollection?) {
        if (collection == null) {
            return
        }
        this.groupCollection = collection
        // collection.date can be null, for instance when using EmptyAccount (no account is registered!)
        // TODO Handle this better. Remove empty account
        if (collection.date != null) {
            this.events = EventStorage.getEventsAt(context, collection.date)
        } else {
            this.events = ArrayList<Event>()
        }
        notifyDataSetChanged()
    }
}