package de.nils_beyer.android.Vertretungen.mainActivity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.account.Account
import de.nils_beyer.android.Vertretungen.account.Dataset
import de.nils_beyer.android.Vertretungen.data.GroupCollection
import de.nils_beyer.android.Vertretungen.events.Event
import de.nils_beyer.android.Vertretungen.events.EventStorage
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.EventViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.GroupViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.ImmediacityViewHolder
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.MOTDHolder

class OverviewAdapter(private val context: Context, private var groupCollection: GroupCollection, private val account : Dataset) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class ViewTypes { GroupViewHolder, ImmediacityViewHolder, EventsViewHolder, MOTDHolder }

    private var events : List<Event> = loadEvents()
    private var motd : String? = account.getMOTD(context)



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (motd == null) {
            when (holder) {
                is GroupViewHolder -> holder.bind(groupCollection, position - events.size - 1)
                is ImmediacityViewHolder -> holder.bind(groupCollection.immediacity)
                is EventViewHolder -> holder.bind(events[position])
            }
        } else {
            when (holder) {
                is GroupViewHolder -> holder.bind(groupCollection, position - events.size)
                is ImmediacityViewHolder -> holder.bind(groupCollection.immediacity)
                is EventViewHolder -> holder.bind(events[position - 1])
                is MOTDHolder -> holder.bind(motd!!)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (motd == null) {
            val eventsCount = events.size
            return when {
                position < eventsCount -> ViewTypes.EventsViewHolder.ordinal
                position == eventsCount -> ViewTypes.ImmediacityViewHolder.ordinal
                else -> ViewTypes.GroupViewHolder.ordinal
            }
        } else {
            val eventsCount = events.size
            return when {
                position == 0 -> ViewTypes.MOTDHolder.ordinal
                position < eventsCount + 1 -> ViewTypes.EventsViewHolder.ordinal
                position == eventsCount + 1 -> ViewTypes.ImmediacityViewHolder.ordinal
                else -> ViewTypes.GroupViewHolder.ordinal
            }
        }
    }

    override fun getItemCount(): Int {
        var count = events.size
        if (groupCollection.groupArrayList.size != 0) {
            count += groupCollection.groupArrayList.size + 1
        }
        if (motd != null) {
            Log.d("MOTD", "MOTD fount: " + motd)
            count += 1
        }
        return count
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
            ViewTypes.MOTDHolder.ordinal -> {
                val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_overview_motd, parent, false)
                return MOTDHolder(v, context)
            }
            else -> TODO("not yet implemented?!")
        }
    }

    fun update(collection: GroupCollection?) {
        if (collection == null) {
            return
        }
        this.groupCollection = collection
        this.motd = account.getMOTD(context)
        loadEvents()

        notifyDataSetChanged()
    }

    private fun loadEvents() : List<Event> {
        // collection.date can be null, for instance when using EmptyAccount (no account is registered!)
        // TODO Handle this better. Remove empty account
        if (groupCollection.date != null) {
            this.events = EventStorage.getEventsAt(context, groupCollection.date)
        } else {
            this.events = ArrayList<Event>()
        }
        return this.events
    }
}