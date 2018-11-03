package de.nils_beyer.android.Vertretungen.mainActivity


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.nils_beyer.android.Vertretungen.R
import de.nils_beyer.android.Vertretungen.events.Event
import de.nils_beyer.android.Vertretungen.events.EventStorage
import de.nils_beyer.android.Vertretungen.mainActivity.viewholder.EventViewHolder


/**
 * Displays the list of events provided by [EventStorage]
 */
class EventListFragment : Fragment() {

    private var eventAdapter : EventListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        eventAdapter = EventListAdapter(context!!) // the context cannot be null.
        val rootView = inflater.inflate(R.layout.fragment_event_list, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.event_view_recyclerview)
        // setup the recyclerView
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context) // set the layout
            adapter = eventAdapter // set the inner adapter class
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL)) // add a horizontal line
        }

        return rootView;
    }

    /**
     * Allows other components to update the EventListFragment
     * This will invoke an update, so new events will be loaded from the event storage
     * and will be displayed in the fragment
     */
    fun update() {
        eventAdapter?.update()
    }


    private class EventListAdapter(val c : Context) : RecyclerView.Adapter<EventViewHolder>() {
        var events : List<Event> = fetchEvents(c);

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): EventViewHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_overview_event, parent, false)
            return EventViewHolder(v, c)
        }

        override fun getItemCount(): Int = events.size

        override fun onBindViewHolder(viewHolder: EventViewHolder, position: Int) {
            viewHolder.bind(events.get(position))
        }

        fun update() {
            // Fetch new events
            events = fetchEvents(c)
            notifyDataSetChanged()
        }

        /**
         * Describes the data model of the EventListAdapter. Will be called on initialization and update
         */
        fun fetchEvents(c : Context) = EventStorage.get(c).sortedBy { it.start }
    }
}
