package de.nils_beyer.android.Vertretungen.events

import java.util.*

data class Event(val start : Date, val end : Date?, val timeInformation : String?, val title : String) {
    fun intersects(date : Date) : Boolean {
        if (start.sameDay(date)) {
            return true
        } else if (end != null) {
            if (end.sameDay(date)) {
                return true
            } else if (date.after(start) && date.before(end)) {
                return true
            }
        }
        return false
    }
}

fun Date.sameDay(otherDay : Date) : Boolean {
    val c1 = Calendar.getInstance()
    val c2 = Calendar.getInstance()

    c1.time = this
    c2.time = this

    if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
        return true
    } else {
        return false
    }
}