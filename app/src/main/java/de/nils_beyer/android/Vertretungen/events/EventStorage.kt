package de.nils_beyer.android.Vertretungen.events

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*
import com.google.gson.reflect.TypeToken



private const val KEY_PREFERENCE_STORAGE = "KEY_PREFERENCE_STORAGE"
private const val EVENTS_KEY = "STORAGE_KEY_FOR_EVENTS2"

object EventStorage {
    fun save(context: Context, events: List<Event>) {
        sharedPreferences(context) {
            it.edit().putString(EVENTS_KEY, Gson().toJson(events)).apply()
        }
    }

    fun get(context: Context): List<Event> {
        sharedPreferences(context) {
            val string = it.getString(EVENTS_KEY, "")
            val listType = object : TypeToken<List<Event>>() {}.type
            val res : List<Event>? = Gson().fromJson(string, listType)
            if (res == null) {
                return ArrayList<Event>()
            } else {
                return res
            }

        }
    }

    fun getEventsAt(context : Context, date: Date) : List<Event> {
        return get(context).filter {
            it.intersects(date)
        }
    }
}

/**
 * Inline function to access shared preferences storage
 */
private inline fun <T> sharedPreferences(c : Context, body: (SharedPreferences) -> T) : T {
    val sharedPreferences = c.getSharedPreferences(KEY_PREFERENCE_STORAGE, Context.MODE_PRIVATE)
    return body(sharedPreferences)
}

private fun List<Event>.parseToJsonSet() : Set<String> {
    val stringSet = HashSet<String>()
    this.forEach {
        stringSet.add(Gson().toJson(it))
    }
    return stringSet
}