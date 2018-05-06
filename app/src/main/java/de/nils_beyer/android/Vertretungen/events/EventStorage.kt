package de.nils_beyer.android.Vertretungen.events

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

private const val KEY_PREFERENCE_STORAGE = "KEY_PREFERENCE_STORAGE"
private const val EVENTS_KEY = "STORAGE_KEY_FOR_EVENTS"

object EventStorage {
    fun save(context: Context, events: List<Event>) {
        sharedPreferences(context) {
            it.edit().putStringSet(EVENTS_KEY, events.parseToJsonSet()).apply()
        }
    }

    fun get(context: Context): List<Event> {
        sharedPreferences(context) {
            val stringSet = it.getStringSet(EVENTS_KEY, HashSet<String>())
            return stringSet.map {
                Gson().fromJson(it, Event::class.java)
            }
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